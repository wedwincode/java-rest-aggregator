package ru.wedwin.aggregator.adapter.in.cli;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.app.api.ApiRegistry;
import ru.wedwin.aggregator.app.codec.CodecRegistry;
import ru.wedwin.aggregator.app.port.in.StartAggregationUseCase;
import ru.wedwin.aggregator.app.port.in.StopAggregationUseCase;
import ru.wedwin.aggregator.app.port.in.ViewResultsUseCase;
import ru.wedwin.aggregator.app.session.Session;
import ru.wedwin.aggregator.domain.config.ExecutionSpec;
import ru.wedwin.aggregator.domain.config.RunConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.Map;

import static org.mockito.Mockito.*;

public class CliAppTest {

    @Test
    void givenNonInteractive_whenRun_thenStartStopAndViewAreCalled() {
        ApiRegistry apiRegistry = mock(ApiRegistry.class);
        CodecRegistry codecRegistry = mock(CodecRegistry.class);
        StartAggregationUseCase start = mock(StartAggregationUseCase.class);
        StopAggregationUseCase stop = mock(StopAggregationUseCase.class);
        ViewResultsUseCase view = mock(ViewResultsUseCase.class);
        Session session = mock(Session.class);
        RunConfig config = mockRunConfig(Duration.ofMillis(200));

        when(start.start(any())).thenReturn(session);

        CliApp app = new CliApp(
                new String[]{},
                apiRegistry,
                codecRegistry,
                new ByteArrayInputStream(new byte[0]),
                new PrintStream(new ByteArrayOutputStream()),
                start,
                stop,
                view
        ) {
            @Override
            protected RunConfig getRunConfig() {
                return config;
            }
        };

        app.run();

        verify(start).start(config);
        verify(stop).stop(session);
        verify(view).view(config);
    }

    @Test
    void givenInteractiveAndStopCommand_whenRun_thenStopsEarly() {
        ApiRegistry apiRegistry = mock(ApiRegistry.class);
        CodecRegistry codecRegistry = mock(CodecRegistry.class);
        StartAggregationUseCase start = mock(StartAggregationUseCase.class);
        StopAggregationUseCase stop = mock(StopAggregationUseCase.class);
        ViewResultsUseCase view = mock(ViewResultsUseCase.class);
        Session session = mock(Session.class);
        RunConfig config = mockRunConfig(Duration.ofSeconds(5));

        when(start.start(any())).thenReturn(session);

        ByteArrayInputStream in = new ByteArrayInputStream("stop\n".getBytes());

        CliApp app = new CliApp(
                new String[]{"--interactive"},
                apiRegistry,
                codecRegistry,
                in,
                new PrintStream(new ByteArrayOutputStream()),
                start,
                stop,
                view
        ) {
            @Override
            protected RunConfig getRunConfig() {
                this.isInteractive = true;
                return config;
            }
        };

        app.run();

        verify(start).start(config);
        verify(stop).stop(session);
        verify(view).view(config);
    }

    @Test
    void givenStartThrows_whenRun_thenStopStillCalled() {
        ApiRegistry apiRegistry = mock(ApiRegistry.class);
        CodecRegistry codecRegistry = mock(CodecRegistry.class);
        StartAggregationUseCase start = mock(StartAggregationUseCase.class);
        StopAggregationUseCase stop = mock(StopAggregationUseCase.class);
        ViewResultsUseCase view = mock(ViewResultsUseCase.class);
        Session session = mock(Session.class);
        RunConfig config = mockRunConfig(Duration.ofMillis(100));

        when(start.start(any())).thenReturn(session);

        CliApp app = new CliApp(
                new String[]{},
                apiRegistry,
                codecRegistry,
                new ByteArrayInputStream(new byte[0]),
                new PrintStream(new ByteArrayOutputStream()),
                start,
                stop,
                view
        ) {
            @Override
            protected RunConfig getRunConfig() {
                return config;
            }
        };

        app.run();

        verify(stop).stop(session);
    }

    private static RunConfig mockRunConfig(Duration duration) {
        RunConfig config = mock(RunConfig.class);
        ExecutionSpec executionSpec = mock(ExecutionSpec.class);

        when(config.executionSpec()).thenReturn(executionSpec);
        when(executionSpec.duration()).thenReturn(duration);
        when(config.queryParamsByApi()).thenReturn(Map.of());

        return config;
    }
}