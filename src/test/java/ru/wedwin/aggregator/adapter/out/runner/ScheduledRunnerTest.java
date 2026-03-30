package ru.wedwin.aggregator.adapter.out.runner;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.app.api.ApiRegistry;
import ru.wedwin.aggregator.app.port.out.ApiClient;
import ru.wedwin.aggregator.app.port.out.HttpExecutor;
import ru.wedwin.aggregator.app.session.Session;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.api.ApiParams;
import ru.wedwin.aggregator.domain.codec.CodecId;
import ru.wedwin.aggregator.domain.config.DisplayMode;
import ru.wedwin.aggregator.domain.config.DisplaySpec;
import ru.wedwin.aggregator.domain.config.ExecutionSpec;
import ru.wedwin.aggregator.domain.config.OutputSpec;
import ru.wedwin.aggregator.domain.config.RunConfig;
import ru.wedwin.aggregator.domain.config.WriteMode;
import ru.wedwin.aggregator.domain.result.AggregatedItem;
import ru.wedwin.aggregator.domain.result.Payload;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScheduledRunnerTest {

    @Test
    void givenRegisteredApi_whenStart_thenDeliversResultToOnResult() throws Exception {
        ApiRegistry registry = mock(ApiRegistry.class);
        HttpExecutor httpExecutor = mock(HttpExecutor.class);
        ApiClient client = mock(ApiClient.class);

        ApiId apiId = new ApiId("api1");
        ApiParams params = ApiParams.of(Map.of("q", "java"));
        AggregatedItem expectedItem = new AggregatedItem(apiId, new Payload.PInt(42));

        when(registry.get(apiId)).thenReturn(client);
        when(client.getApiResponse(same(params), same(httpExecutor))).thenReturn(expectedItem);

        ScheduledRunner runner = new ScheduledRunner(registry, httpExecutor);
        CountDownLatch resultLatch = new CountDownLatch(1);
        AtomicReference<AggregatedItem> actualResult = new AtomicReference<>();
        AtomicReference<Throwable> actualError = new AtomicReference<>();

        Session session = runner.start(
                buildConfig(apiId, params),
                item -> {
                    actualResult.set(item);
                    resultLatch.countDown();
                },
                actualError::set
        );

        try {
            assertTrue(resultLatch.await(2, TimeUnit.SECONDS));

            assertSame(expectedItem, actualResult.get());
            assertNull(actualError.get());
        } finally {
            runner.stop(session);
        }
    }

    @Test
    void givenClientFailure_whenStart_thenDeliversCauseToOnError() throws Exception {
        ApiRegistry registry = mock(ApiRegistry.class);
        HttpExecutor httpExecutor = mock(HttpExecutor.class);
        ApiClient client = mock(ApiClient.class);

        ApiId apiId = new ApiId("api1");
        ApiParams params = ApiParams.of(Map.of("q", "java"));
        RuntimeException expectedError = new RuntimeException("boom");

        when(registry.get(apiId)).thenReturn(client);
        when(client.getApiResponse(same(params), same(httpExecutor))).thenThrow(expectedError);

        ScheduledRunner runner = new ScheduledRunner(registry, httpExecutor);
        CountDownLatch errorLatch = new CountDownLatch(1);
        AtomicReference<AggregatedItem> actualResult = new AtomicReference<>();
        AtomicReference<Throwable> actualError = new AtomicReference<>();

        Session session = runner.start(
                buildConfig(apiId, params),
                actualResult::set,
                error -> {
                    actualError.set(error);
                    errorLatch.countDown();
                }
        );

        try {
            assertTrue(errorLatch.await(2, TimeUnit.SECONDS));

            assertNull(actualResult.get());
            assertSame(expectedError, actualError.get());
        } finally {
            runner.stop(session);
        }
    }

    private static RunConfig buildConfig(ApiId apiId, ApiParams params) {
        return new RunConfig(
                Map.of(apiId, params),
                new OutputSpec(Path.of("out.json"), new CodecId("json"), WriteMode.NEW),
                new ExecutionSpec(1, Duration.ofSeconds(1), Duration.ofSeconds(5)),
                new DisplaySpec(DisplayMode.ALL)
        );
    }
}
