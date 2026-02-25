package ru.wedwin.aggregator.adapter.out.common;

import ru.wedwin.aggregator.domain.model.Payload;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class JacksonObjectMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectMapper instance() { // todo factory?
        return mapper;
    }

    public static <T> T map(String content, Class<T> clazz) {
        try {
            return mapper.readValue(content, clazz);
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to deserialize JSON to " + clazz.getSimpleName(), e);
        }
    }

    public static <T> Payload fromDto(T dto) {
        JsonNode node = mapper.valueToTree(dto);
        return PayloadMapper.fromJsonNode(node);
    }
}