package ru.wedwin.aggregator.app;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.wedwin.aggregator.app.service.api.ApiRegistry;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.codec.CodecId;
import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.domain.model.output.DisplayMode;
import ru.wedwin.aggregator.domain.model.output.DisplaySpec;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.output.WriteMode;
import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.port.in.RunConfigProvider;
import ru.wedwin.aggregator.port.out.ApiClient;
import ru.wedwin.aggregator.port.out.Executor;
import ru.wedwin.aggregator.port.out.ResultSaver;
import ru.wedwin.aggregator.port.out.ResultViewer;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AggregationUseCaseTest {

    @Mock RunConfigProvider runConfigProvider;
    @Mock Executor executor;
    @Mock ApiRegistry apiRegistry;
    @Mock ResultSaver saver;
    @Mock ResultViewer viewer;

    @Mock ApiClient apiClient1;
    @Mock ApiClient apiClient2;

    @InjectMocks AggregationUseCase useCase;

    @Test
    void run_SavesAllResponsesAndDoesNotPrint_WhenDisplayNone() throws Exception {
        ApiId api1 = new ApiId("api1");
        ApiId api2 = new ApiId("api2");

        Map<ApiId, ApiParams> qp = new LinkedHashMap<>();
        qp.put(api1, ApiParams.of(Map.of("q", "1")));
        qp.put(api2, ApiParams.of());

        CodecId codec1 = new CodecId("codec1");

        OutputSpec out = new OutputSpec(Path.of("out.json"), codec1, WriteMode.NEW);
        DisplaySpec display = new DisplaySpec(api1, DisplayMode.NONE);

        RunConfig cfg = new RunConfig(qp, out, display);

        when(runConfigProvider.getRunConfig()).thenReturn(cfg);

        when(apiRegistry.getClient(api1)).thenReturn(apiClient1);
        when(apiRegistry.getClient(api2)).thenReturn(apiClient2);

        AggregatedItem item1 = mock(AggregatedItem.class);
        AggregatedItem item2 = mock(AggregatedItem.class);

        when(apiClient1.getApiResponse(any(), eq(executor))).thenReturn(item1);
        when(apiClient2.getApiResponse(any(), eq(executor))).thenReturn(item2);

        useCase.run();

        verify(apiRegistry).getClient(api1);
        verify(apiRegistry).getClient(api2);

        verify(apiClient1).getApiResponse(eq(qp.get(api1)), eq(executor));
        verify(apiClient2).getApiResponse(eq(qp.get(api2)), eq(executor));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AggregatedItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(saver).save(eq(out), captor.capture());
        assertEquals(List.of(item1, item2), captor.getValue());

        verifyNoInteractions(viewer);
    }

    @Test
    void run_PrintsAll_WhenDisplayAll() throws Exception {
        ApiId api1 = new ApiId("api1");
        CodecId codec1 = new CodecId("codec1");

        Map<ApiId, ApiParams> qp = Map.of(api1, ApiParams.of());
        OutputSpec out = new OutputSpec(Path.of("out.json"), codec1, WriteMode.NEW);
        DisplaySpec display = new DisplaySpec(api1, DisplayMode.ALL);

        RunConfig cfg = new RunConfig(qp, out, display);

        when(runConfigProvider.getRunConfig()).thenReturn(cfg);
        when(apiRegistry.getClient(api1)).thenReturn(apiClient1);
        when(apiClient1.getApiResponse(any(), any())).thenReturn(mock(AggregatedItem.class));

        useCase.run();

        verify(viewer).all(out);
        verify(viewer, never()).byApi(any(), any());
    }

    @Test
    void run_PrintsByApi_WhenDisplayByApi() throws Exception {
        ApiId api1 = new ApiId("api1");
        CodecId codec1 = new CodecId("codec1");

        Map<ApiId, ApiParams> qp = Map.of(api1, ApiParams.of());
        OutputSpec out = new OutputSpec(Path.of("out.json"), codec1, WriteMode.NEW);
        DisplaySpec display = new DisplaySpec(api1, DisplayMode.BY_API);

        RunConfig cfg = new RunConfig(qp, out, display);

        when(runConfigProvider.getRunConfig()).thenReturn(cfg);
        when(apiRegistry.getClient(api1)).thenReturn(apiClient1);
        when(apiClient1.getApiResponse(any(), any())).thenReturn(mock(AggregatedItem.class));

        useCase.run();

        verify(viewer).byApi(out, api1);
        verify(viewer, never()).all(any());
    }
}