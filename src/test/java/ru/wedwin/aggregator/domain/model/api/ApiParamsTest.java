package ru.wedwin.aggregator.domain.model.api;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.api.ApiParams;
import ru.wedwin.aggregator.domain.api.ParamMeta;
import ru.wedwin.aggregator.domain.api.exception.InvalidApiParamsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApiParamsTest {

    static final List<ParamMeta> defaultParams = List.of(
            new ParamMeta("key1", true, "default", "desc"),
            new ParamMeta("key2", false, "string", "1234"),
            new ParamMeta("key3", true, "1234", "5678")
    );

    @Test
    void createFromNothingAndGet() {
        ApiParams params = ApiParams.of();
        Map<String, String> asMap = params.asMap();

        assertEquals(Map.of(), asMap);
    }

    @Test
    void createFromFilledMapAndGet() { // todo naming
        Map<String, String> map = new HashMap<>();
        map.put("p1", "v1");
        map.put("p2", "v2");

        ApiParams params = ApiParams.of(map);
        assertEquals(map, params.asMap()); // todo is it ok to compare this way
    }

    @Test
    void createFromNullAndGet() {
        ApiParams params = ApiParams.of(null);
        assertEquals(Map.of(), params.asMap());
    }

    @Test
    void putNullKey() {
        ApiParams params = ApiParams.of();
        assertThrows(InvalidApiParamsException.class, () -> params.put(null, "v1"));
    }

    @Test
    void putBlankKey() {
        ApiParams params = ApiParams.of();
        assertThrows(InvalidApiParamsException.class, () -> params.put("  ", "v1"));
    }

    @Test
    void putNullValue() {
        ApiParams params = ApiParams.of();
        assertThrows(InvalidApiParamsException.class, () -> params.put("p1", null));
    }

    @Test
    void putBlankValue() {
        ApiParams params = ApiParams.of();
        assertThrows(InvalidApiParamsException.class, () -> params.put("p1", "  "));
    }

    @Test
    void putNewValue() {
        ApiParams params = ApiParams.of();
        assertNull(params.put("p1", "v1"));
    }

    @Test
    void putNewValueWithReplace() {
        ApiParams params = ApiParams.of();
        params.put("p1", "v1");

        assertEquals("v1", params.put("p1", "v2"));
    }

    @Test
    void addDefaultParamsNull() {
        ApiParams params = ApiParams.of();
        params.addDefaultParams(null);

        assertEquals(Map.of(), params.asMap());
    }

    @Test
    void addDefaultParamsNullValue() {
        ApiParams params = ApiParams.of();
        List<ParamMeta> invalid = List.of(new ParamMeta("key1", true, null, "desc"));

        assertThrows(InvalidApiParamsException.class, () -> params.addDefaultParams(invalid));
    }

    @Test
    void addDefaultParamsBlankValue() {
        ApiParams params = ApiParams.of();
        List<ParamMeta> invalid = List.of(new ParamMeta("key1", true, "   ", "desc"));

        assertThrows(InvalidApiParamsException.class, () -> params.addDefaultParams(invalid));
    }

    @Test
    void addDefaultParamsCorrect() {
        ApiParams params = ApiParams.of();
        params.addDefaultParams(defaultParams);

        Map<String, String> expected = Map.of(
                defaultParams.getFirst().key(), defaultParams.getFirst().defaultValue(),
                defaultParams.getLast().key(), defaultParams.getLast().defaultValue()

        );

        assertEquals(expected, params.asMap());
    }

    @Test
    void addDefaultParamsCorrectAlreadyHasParam() {
        ApiParams params = ApiParams.of();
        params.put(defaultParams.getFirst().key(), "oldValue");
        params.addDefaultParams(defaultParams);

        Map<String, String> expected = Map.of(
                defaultParams.getFirst().key(), "oldValue",
                defaultParams.getLast().key(), defaultParams.getLast().defaultValue()

        );

        assertEquals(expected, params.asMap());
    }


}
