package ru.wedwin.aggregator.adapter.out.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.wedwin.aggregator.app.api.ApiRegistry;
import ru.wedwin.aggregator.app.session.Session;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.api.ApiParams;
import ru.wedwin.aggregator.domain.config.RunConfig;
import ru.wedwin.aggregator.domain.result.AggregatedItem;
import ru.wedwin.aggregator.port.out.Runner;
import ru.wedwin.aggregator.port.out.ApiClient;
import ru.wedwin.aggregator.port.out.HttpExecutor;

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

public class ScheduledRunner implements Runner {

    private static final Logger log = LogManager.getLogger(ScheduledRunner.class);
    private final ScheduledExecutorService scheduler;
    private final ExecutorService workers;
    private final ApiRegistry registry;
    private final HttpExecutor httpExecutor;
    private int nextIndex = 0;

    private record ApiTask(ApiClient client, ApiParams params) {}

    public ScheduledRunner(ApiRegistry registry, HttpExecutor httpExecutor) {
        this.registry = registry;
        this.httpExecutor = httpExecutor;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        // number of threads is not restricted, so we use semaphore inside handle to explicitly restrict it
        this.workers = Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    public Session start(
            RunConfig config,
            Consumer<AggregatedItem> onResult,
            Consumer<Throwable> onError
    ) {
        Session session = new Session(config.executionSpec().maxConcurrentTasks());
        List<ApiTask> tasks = buildTasks(config.queryParamsByApi());
        ScheduledFuture<?> trigger = scheduler.scheduleAtFixedRate(
                () -> dispatch(session, tasks, onResult, onError),
                0,
                config.executionSpec().pollInterval().toMillis(),
                TimeUnit.MILLISECONDS
        );
        session.setDispatchTrigger(trigger);
        return session;
    }

    private List<ApiTask> buildTasks(Map<ApiId, ApiParams> queryParamsByApi) {
        List<ApiTask> tasks = new ArrayList<>();

        for (ApiId id: queryParamsByApi.keySet()) {
            ApiClient client = registry.get(id);
            ApiParams params = queryParamsByApi.getOrDefault(id, ApiParams.of());
            tasks.add(new ApiTask(client, params));
        }

        return tasks;
    }

    private void dispatch(
            Session session,
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
            if (!session.tryAcquireExecutionSlot()) {
                return;
            }

            // initially this block was before the acquiring so the order of execution was random
            // because index was changing no matter of the acquiring decision
            ApiTask task = tasks.get(nextIndex);
            nextIndex = (nextIndex + 1) % size;
            checked++;

            launchTask(session, task, onResult, onError);
            log.debug("launched task {}", task.client().id());
        }
    }

    private void launchTask(
            Session session,
            ApiTask task,
            Consumer<AggregatedItem> onResult,
            Consumer<Throwable> onError
    ) {
        CompletableFuture // workers.submit is an alternative but not so cool
                .supplyAsync(() -> task.client().getApiResponse(task.params(), httpExecutor), workers)
                .whenComplete((result, error) -> {
                    try {
                        if (error != null) {
                            Throwable actualError = error.getCause() != null ? error.getCause() : error;
                            onError.accept(actualError);
                            return;
                        }
                        onResult.accept(result);
                    } finally {
                        session.releaseExecutionSlot();
                    }
                });
    }

//    private void launchTask(
//            Session session,
//            ApiTask task,
//            Consumer<AggregatedItem> onResult,
//            Consumer<Throwable> onError
//    ) {
//        workers.submit(() -> {
//            try {
//                AggregatedItem result = task.client().getApiResponse(task.params(), httpExecutor);
//                onResult.accept(result);
//            } catch (Throwable error) {
//                onError.accept(error);
//            } finally {
//                session.completeTask();
//            }
//        });
//    }

    @Override
    public void stop(Session session) {
        session.stopScheduling();

        while (session.hasRunningExecutions()) {
            try {
                //noinspection BusyWait
                sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        scheduler.shutdown();
        workers.shutdown();

        try {
            boolean schedulerStopped = scheduler.awaitTermination(5, TimeUnit.SECONDS);
            boolean workersStopped = workers.awaitTermination(5, TimeUnit.SECONDS);
            if (!schedulerStopped) {
                log.warn("scheduler did not terminate within timeout");
            }
            if (!workersStopped) {
                log.warn("workers did not terminate within timeout");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
