package ru.wedwin.aggregator.app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.wedwin.aggregator.app.port.out.ResultSaver;
import ru.wedwin.aggregator.app.port.out.ResultViewer;
import ru.wedwin.aggregator.app.port.out.Runner;
import ru.wedwin.aggregator.app.session.Session;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.config.DisplayMode;
import ru.wedwin.aggregator.domain.config.DisplaySpec;
import ru.wedwin.aggregator.domain.config.OutputSpec;
import ru.wedwin.aggregator.domain.config.RunConfig;
import ru.wedwin.aggregator.domain.config.WriteMode;
import ru.wedwin.aggregator.domain.result.AggregatedItem;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AggregationServiceTest {

    @Mock ResultSaver saver;
    @Mock ResultViewer viewer;
    @Mock Runner runner;
    @Mock RunConfig runConfig;
    @Mock OutputSpec outputSpec;

    @InjectMocks AggregationService service;

    @Test
    void givenValidConfig_whenStart_thenDelegatesToRunnerAndReturnsSession() {
        Session session = mock(Session.class);

        when(runner.start(eq(runConfig), any(), any())).thenReturn(session);

        Session result = service.start(runConfig);

        assertSame(session, result);
        verify(runner).start(eq(runConfig), any(), any());
    }

    @Test
    void givenNewMode_whenStart_thenSavesSnapshotResults() {
        AggregatedItem first = mock(AggregatedItem.class);
        AggregatedItem second = mock(AggregatedItem.class);

        when(first.apiId()).thenReturn(new ApiId("api-1"));
        when(second.apiId()).thenReturn(new ApiId("api-2"));

        when(runConfig.outputSpec()).thenReturn(outputSpec);
        when(outputSpec.mode()).thenReturn(WriteMode.NEW);

        when(runner.start(eq(runConfig), any(), any())).thenAnswer(invocation -> {
            Consumer<AggregatedItem> onResult = invocation.getArgument(1);
            onResult.accept(first);
            onResult.accept(second);
            return mock(Session.class);
        });

        service.start(runConfig);

        verify(viewer).progress(new ApiId("api-1"));
        verify(viewer).progress(new ApiId("api-2"));

        verify(saver).save(outputSpec, List.of(first));
        verify(saver).save(outputSpec, List.of(first, second));
    }

    @Test
    void givenAppendMode_whenStart_thenSavesOnlyCurrentItem() {
        AggregatedItem first = mock(AggregatedItem.class);
        AggregatedItem second = mock(AggregatedItem.class);

        when(first.apiId()).thenReturn(new ApiId("api-1"));
        when(second.apiId()).thenReturn(new ApiId("api-2"));

        when(runConfig.outputSpec()).thenReturn(outputSpec);
        when(outputSpec.mode()).thenReturn(WriteMode.APPEND);

        when(runner.start(eq(runConfig), any(), any())).thenAnswer(invocation -> {
            Consumer<AggregatedItem> onResult = invocation.getArgument(1);
            onResult.accept(first);
            onResult.accept(second);
            return mock(Session.class);
        });

        service.start(runConfig);

        verify(viewer).progress(new ApiId("api-1"));
        verify(viewer).progress(new ApiId("api-2"));

        verify(saver).save(outputSpec, List.of(first));
        verify(saver).save(outputSpec, List.of(second));
        verify(saver, never()).save(outputSpec, List.of(first, second));
    }

    @Test
    void givenRunnerError_whenStart_thenForwardsErrorToViewer() {
        RuntimeException error = new RuntimeException("boom");

        when(runner.start(eq(runConfig), any(), any())).thenAnswer(invocation -> {
            Consumer<Throwable> onError = invocation.getArgument(2);
            onError.accept(error);
            return mock(Session.class);
        });

        service.start(runConfig);

        verify(viewer).error(error);
    }

    @Test
    void givenSession_whenStop_thenDelegatesToRunner() {
        Session session = mock(Session.class);

        service.stop(session);

        verify(runner).stop(session);
    }

    @Test
    void givenDisplayModeNone_whenView_thenDoesNothing() {
        DisplaySpec displaySpec = mock(DisplaySpec.class);

        when(runConfig.displaySpec()).thenReturn(displaySpec);
        when(displaySpec.mode()).thenReturn(DisplayMode.NONE);

        service.view(runConfig);

        verifyNoInteractions(viewer);
    }

    @Test
    void givenDisplayModeAll_whenView_thenShowsAllResults() {
        DisplaySpec displaySpec = mock(DisplaySpec.class);

        when(runConfig.displaySpec()).thenReturn(displaySpec);
        when(runConfig.outputSpec()).thenReturn(outputSpec);
        when(displaySpec.mode()).thenReturn(DisplayMode.ALL);

        service.view(runConfig);

        verify(viewer).all(outputSpec);
    }

    @Test
    void givenDisplayModeByApi_whenView_thenShowsResultsForSpecificApi() {
        DisplaySpec displaySpec = mock(DisplaySpec.class);

        when(runConfig.displaySpec()).thenReturn(displaySpec);
        when(runConfig.outputSpec()).thenReturn(outputSpec);
        when(displaySpec.mode()).thenReturn(DisplayMode.BY_API);
        when(displaySpec.apiId()).thenReturn(new ApiId("api-42"));

        service.view(runConfig);

        verify(viewer).byApi(outputSpec, new ApiId("api-42"));
    }
}
