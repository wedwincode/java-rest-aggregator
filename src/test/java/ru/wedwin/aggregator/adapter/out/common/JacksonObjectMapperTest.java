package ru.wedwin.aggregator.adapter.out.common;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.result.Payload;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JacksonObjectMapperTest {

    @Test
    void givenValidJson_whenMap_thenReturnsObject() {
        String json = """
                {"value":123}
                """;
        TestDto dto = JacksonObjectMapper.map(json, TestDto.class);

        assertEquals(123, dto.value);
    }

    @Test
    void givenInvalidJson_whenMap_thenThrows() {
        String json = "{invalid json}";

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> JacksonObjectMapper.map(json, TestDto.class));
        assertTrue(ex.getMessage().contains("Failed to deserialize JSON"));
        assertTrue(ex.getMessage().contains("raw body"));
    }

    @Test
    void givenDto_whenFromDto_thenReturnsPayload() {
        TestDto dto = new TestDto();
        dto.value = 42;
        Payload payload = JacksonObjectMapper.fromDto(dto);
        Payload.PObject expectedPayload = new Payload.PObject(Map.of("value", new Payload.PInt(42)));

        assertInstanceOf(Payload.PObject.class, payload);
        assertEquals(expectedPayload, (Payload.PObject) payload);
    }

    @Test
    void givenComplexDto_whenFromDto_thenConvertsToPayloadObject() {
        ComplexDto dto = new ComplexDto();
        dto.name = "test";
        dto.value = 10;
        Payload payload = JacksonObjectMapper.fromDto(dto);

        assertInstanceOf(Payload.PObject.class, payload);
    }

    public static class TestDto {
        public int value;
    }

    public static class ComplexDto {
        public String name;
        public int value;
    }
}