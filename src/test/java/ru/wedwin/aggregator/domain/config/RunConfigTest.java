package ru.wedwin.aggregator.domain.config;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.api.ApiParams;
import ru.wedwin.aggregator.domain.codec.CodecId;
import ru.wedwin.aggregator.domain.config.exception.InvalidRunConfigException;

import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RunConfigTest {

    private static final Map<ApiId, ApiParams> PARAMS = buildParams();
    private static final OutputSpec OUTPUT_SPEC =
            new OutputSpec(Path.of("/"), new CodecId("csv"), WriteMode.NEW);
    private static final ExecutionSpec EXECUTION_SPEC =
            new ExecutionSpec(5, Duration.ofSeconds(5), Duration.ofSeconds(10));
    private static final DisplaySpec DISPLAY_SPEC =
            new DisplaySpec(DisplayMode.ALL);

    @Test
    void givenValidValues_whenCreateRunConfig_thenFieldsAreSet() {
        RunConfig config = new RunConfig(PARAMS, OUTPUT_SPEC, EXECUTION_SPEC, DISPLAY_SPEC);

        assertEquals(PARAMS, config.queryParamsByApi());
        assertEquals(OUTPUT_SPEC, config.outputSpec());
        assertEquals(EXECUTION_SPEC, config.executionSpec());
        assertEquals(DISPLAY_SPEC, config.displaySpec());
    }

    @Test
    void givenNullParams_whenCreateRunConfig_thenThrowsException() {
        assertThrows(InvalidRunConfigException.class,
                () -> new RunConfig(null, OUTPUT_SPEC, EXECUTION_SPEC, DISPLAY_SPEC));
    }

    @Test
    void givenEmptyParams_whenCreateRunConfig_thenThrowsException() {
        assertThrows(InvalidRunConfigException.class,
                () -> new RunConfig(Map.of(), OUTPUT_SPEC, EXECUTION_SPEC, DISPLAY_SPEC));
    }

    @Test
    void givenNullOutputSpec_whenCreateRunConfig_thenThrowsException() {
        assertThrows(InvalidRunConfigException.class,
                () -> new RunConfig(PARAMS, null, EXECUTION_SPEC, DISPLAY_SPEC));
    }

    @Test
    void givenNullExecutionSpec_whenCreateRunConfig_thenThrowsException() {
        assertThrows(InvalidRunConfigException.class,
                () -> new RunConfig(PARAMS, OUTPUT_SPEC, null, DISPLAY_SPEC));
    }

    @Test
    void givenNullDisplaySpec_whenCreateRunConfig_thenThrowsException() {
        assertThrows(InvalidRunConfigException.class,
                () -> new RunConfig(PARAMS, OUTPUT_SPEC, EXECUTION_SPEC, null));
    }

    @Test
    void givenParamsContainingNullValue_whenCreateRunConfig_thenThrowsException() {
        Map<ApiId, ApiParams> paramsWithNull = new HashMap<>();
        paramsWithNull.put(new ApiId("api1"), ApiParams.of(Map.of("p1", "v1", "p2", "v2")));
        paramsWithNull.put(new ApiId("api2"), null);

        assertThrows(InvalidRunConfigException.class,
                () -> new RunConfig(paramsWithNull, OUTPUT_SPEC, EXECUTION_SPEC, DISPLAY_SPEC));
    }

    @Test
    void givenValidParams_whenCreateRunConfig_thenParamsAreImmutableCopy() {
        Map<ApiId, ApiParams> mutable = new HashMap<>(PARAMS);
        RunConfig config = new RunConfig(mutable, OUTPUT_SPEC, EXECUTION_SPEC, DISPLAY_SPEC);

        assertEquals(mutable, config.queryParamsByApi());
        assertThrows(UnsupportedOperationException.class,
                () -> config.queryParamsByApi().put(new ApiId("api3"), ApiParams.of()));
    }

    private static Map<ApiId, ApiParams> buildParams() {
        Map<ApiId, ApiParams> params = new HashMap<>();
        params.put(new ApiId("api1"), ApiParams.of(Map.of("p1", "v1", "p2", "v2")));
        params.put(new ApiId("api2"), ApiParams.of(Map.of("a1", "b1", "a2", "b2")));

        return params;
    }
}
