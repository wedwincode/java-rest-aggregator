package ru.wedwin.aggregator.adapter.out.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.wedwin.aggregator.app.service.api.ApiRegistry;
import ru.wedwin.aggregator.app.service.session.Session;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.port.out.AggregationRunner;
import ru.wedwin.aggregator.port.out.ApiClient;
import ru.wedwin.aggregator.port.out.Executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.lang.Thread.sleep;

public class ScheduledAggregationRunner implements AggregationRunner {
    private static final Logger log = LogManager.getLogger(ScheduledAggregationRunner.class);
    private final ScheduledExecutorService scheduler;
    private final ExecutorService workers;
    private final ApiRegistry registry;
    private final Executor executor;
    private int nextIndex = 0;

    private record ApiTask(ApiClient client, ApiParams params) {}

    public ScheduledAggregationRunner(ApiRegistry registry, Executor executor) {
        this.registry = registry;
        this.executor = executor;
        this.scheduler = Executors.newScheduledThreadPool(1);
        // number of threads is not restricted, so we use semaphore inside handle to explicitly restrict it
        this.workers = Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    public Session start(
            RunConfig config,
            Consumer<AggregatedItem> onResult,
            Consumer<Throwable> onError
    ) {
        Session handle = new Session(config.executionSpec().maxConcurrentTasks());
        List<ApiTask> tasks = buildTasks(config.queryParamsByApi());
        ScheduledFuture<?> scheduledTask = scheduler.scheduleAtFixedRate(
                () -> dispatch(handle, tasks, onResult, onError),
                0,
                config.executionSpec().pollInterval().toMillis(),
                TimeUnit.MILLISECONDS
        );
        handle.addTask(scheduledTask);
        return handle;
    }

    private List<ApiTask> buildTasks(Map<ApiId, ApiParams> queryParamsByApi) {
        List<ApiTask> tasks = new ArrayList<>();

        for (ApiId id: queryParamsByApi.keySet()) {
            ApiClient client = registry.getClient(id);
            ApiParams params = queryParamsByApi.getOrDefault(id, ApiParams.of());
            tasks.add(new ApiTask(client, params));
        }

        return tasks;
    }

    private void dispatch(
            Session handle,
            List<ApiTask> tasks,
            Consumer<AggregatedItem> onResult,
            Consumer<Throwable> onError
    ) {
        if (tasks.isEmpty()) {
            return;
        }

        int checked = 0;
        int size = tasks.size();

        while (checked < size) {
            // todo: описание того что было: раньше был ++ до лока и поэтому thenewsapi имел приоритет
            if (!handle.tryAcquireLaunch()) {
                return;
            }

            ApiTask task = tasks.get(nextIndex);
            nextIndex = (nextIndex + 1) % size;
            checked++;

            launchTask(handle, task, onResult, onError);
            log.debug("launched task {}", task.client().id());
        }
    }

    private void launchTask(
            Session handle,
            ApiTask task,
            Consumer<AggregatedItem> onResult,
            Consumer<Throwable> onError
    ) { // todo print time when started and finished
        CompletableFuture
                .supplyAsync(() -> task.client().getApiResponse(task.params(), executor), workers)
                .whenComplete((result, error) -> {
                    try {
                        if (error != null) {
                            onError.accept(error.getCause());
                            return;
                        }
                        onResult.accept(result);
                    } finally {
                        handle.completeLaunch();
                    }
                });
    }

    @Override
    public void stop(Session handle) {
        handle.stopScheduling();

        while (handle.hasInFlightTasks()) {
            try {
                sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        scheduler.shutdown();
        workers.shutdown();

        try {
            // todo разобраться
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
            workers.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
