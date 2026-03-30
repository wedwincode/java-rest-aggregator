package ru.wedwin.aggregator.domain.api;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.api.exception.InvalidApiParamsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApiParamsTest {

    private static final List<ParamMeta> DEFAULT_PARAMS = List.of(
            new ParamMeta("key1", true, "default", "desc"),
            new ParamMeta("key2", false, "string", "1234"),
            new ParamMeta("key3", true, "1234", "5678")
    );

    @Test
    void givenNoParams_whenCreate_thenReturnsEmptyMap() {
        ApiParams params = ApiParams.of();
        assertEquals(Map.of(), params.asMap());
    }

    @Test
    void givenFilledMap_whenCreate_thenReturnsSameEntries() {
        Map<String, String> map = new HashMap<>();
        map.put("p1", "v1");
        map.put("p2", "v2");

        ApiParams params = ApiParams.of(map);
        assertEquals(map, params.asMap());
    }

    @Test
    void givenNullMap_whenCreate_thenReturnsEmptyMap() {
        ApiParams params = ApiParams.of(null);
        assertEquals(Map.of(), params.asMap());
    }

    @Test
    void givenNullKey_whenPut_thenThrowsException() {
        ApiParams params = ApiParams.of();
        assertThrows(InvalidApiParamsException.class, () -> params.put(null, "v1"));
    }

    @Test
    void givenBlankKey_whenPut_thenThrowsException() {
        ApiParams params = ApiParams.of();
        assertThrows(InvalidApiParamsException.class, () -> params.put("  ", "v1"));
    }

    @Test
    void givenNullValue_whenPut_thenThrowsException() {
        ApiParams params = ApiParams.of();
        assertThrows(InvalidApiParamsException.class, () -> params.put("p1", null));
    }

    @Test
    void givenBlankValue_whenPut_thenThrowsException() {
        ApiParams params = ApiParams.of();
        assertThrows(InvalidApiParamsException.class, () -> params.put("p1", "  "));
    }

    @Test
    void givenNewKey_whenPut_thenReturnsNull() {
        ApiParams params = ApiParams.of();
        assertNull(params.put("p1", "v1"));
    }

    @Test
    void givenExistingKey_whenPut_thenReturnsPreviousValue() {
        ApiParams params = ApiParams.of();
        params.put("p1", "v1");

        assertEquals("v1", params.put("p1", "v2"));
    }

    @Test
    void givenNullDefaultParams_whenAddDefaultParams_thenMapRemainsEmpty() {
        ApiParams params = ApiParams.of();
        params.addDefaultParams(null);

        assertEquals(Map.of(), params.asMap());
    }

    @Test
    void givenRequiredParamWithNullDefaultValue_whenAddDefaultParams_thenThrowsException() {
        ApiParams params = ApiParams.of();
        List<ParamMeta> invalidParams = List.of(new ParamMeta("key1", true, null, "desc"));

        assertThrows(InvalidApiParamsException.class,
                () -> params.addDefaultParams(invalidParams));
    }

    @Test
    void givenRequiredParamWithBlankDefaultValue_whenAddDefaultParams_thenThrowsException() {
        ApiParams params = ApiParams.of();
        List<ParamMeta> invalidParams = List.of(new ParamMeta("key1", true, "   ", "desc"));

        assertThrows(InvalidApiParamsException.class,
                () -> params.addDefaultParams(invalidParams));
    }

    @Test
    void givenMissingRequiredParams_whenAddDefaultParams_thenAddsOnlyRequiredDefaults() {
        ApiParams params = ApiParams.of();
        params.addDefaultParams(DEFAULT_PARAMS);

        Map<String, String> expected = Map.of(
                DEFAULT_PARAMS.getFirst().key(), DEFAULT_PARAMS.getFirst().defaultValue(),
                DEFAULT_PARAMS.getLast().key(), DEFAULT_PARAMS.getLast().defaultValue()

        );

        assertEquals(expected, params.asMap());
    }

    @Test
    void givenExistingRequiredParam_whenAddDefaultParams_thenKeepsExistingValue() {
        ApiParams params = ApiParams.of();
        params.put(DEFAULT_PARAMS.getFirst().key(), "oldValue");
        params.addDefaultParams(DEFAULT_PARAMS);

        Map<String, String> expected = Map.of(
                DEFAULT_PARAMS.getFirst().key(), "oldValue",
                DEFAULT_PARAMS.getLast().key(), DEFAULT_PARAMS.getLast().defaultValue()

        );

        assertEquals(expected, params.asMap());
    }
}
