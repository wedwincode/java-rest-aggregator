package ru.wedwin.aggregator.adapter.out.common;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.result.Payload;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PayloadJsonConverterTest {

    private final ObjectMapper om = new ObjectMapper();

    @Test
    void givenNullNode_whenFromJson_thenReturnsPNull() {
        Payload result = PayloadJsonConverter.fromJson(null);
        assertInstanceOf(Payload.PNull.class, result);
    }

    @Test
    void givenBooleanNode_whenFromJson_thenReturnsPBool() {
        JsonNode node = om.readTree("true");
        Payload result = PayloadJsonConverter.fromJson(node);

        assertInstanceOf(Payload.PBool.class, result);
        assertTrue(((Payload.PBool) result).value());
    }

    @Test
    void givenIntNode_whenFromJson_thenReturnsPInt() {
        JsonNode node = om.readTree("42");
        Payload result = PayloadJsonConverter.fromJson(node);

        assertInstanceOf(Payload.PInt.class, result);
        assertEquals(42, ((Payload.PInt) result).value());
    }

    @Test
    void givenDoubleNode_whenFromJson_thenReturnsPDouble() {
        JsonNode node = om.readTree("1.5");
        Payload result = PayloadJsonConverter.fromJson(node);

        assertInstanceOf(Payload.PDouble.class, result);
        assertEquals(1.5, ((Payload.PDouble) result).value());
    }

    @Test
    void givenStringNode_whenFromJson_thenReturnsPString() {
        JsonNode node = om.readTree("\"hello\"");
        Payload result = PayloadJsonConverter.fromJson(node);

        assertInstanceOf(Payload.PString.class, result);
        assertEquals("hello", ((Payload.PString) result).value());
    }

    @Test
    void givenArrayNode_whenFromJson_thenReturnsPArray() {
        JsonNode node = om.readTree("[1,2]");
        Payload result = PayloadJsonConverter.fromJson(node);

        assertInstanceOf(Payload.PArray.class, result);
        assertEquals(2, ((Payload.PArray) result).items().size());
    }

    @Test
    void givenObjectNode_whenFromJson_thenReturnsPObject() {
        JsonNode node = om.readTree("""
                {"a":1,"b":"x"}
                """);
        Payload result = PayloadJsonConverter.fromJson(node);

        assertInstanceOf(Payload.PObject.class, result);

        Payload.PObject obj = (Payload.PObject) result;
        assertEquals(1, ((Payload.PInt) obj.fields().get("a")).value());
        assertEquals("x", ((Payload.PString) obj.fields().get("b")).value());
    }

    @Test
    void givenPayload_whenToJson_thenProducesCorrectJson() {
        Payload payload = new Payload.PObject(Map.of(
                "a", new Payload.PInt(1),
                "b", new Payload.PString("x")
        ));
        JsonNode node = PayloadJsonConverter.toJson(payload);

        assertEquals(1, node.get("a").intValue());
        assertEquals("x", node.get("b").asString());
    }

    @Test
    void givenArrayPayload_whenToJson_thenProducesArray() {
        Payload payload = new Payload.PArray(List.of(
                new Payload.PInt(1),
                new Payload.PInt(2)
        ));
        JsonNode node = PayloadJsonConverter.toJson(payload);

        assertTrue(node.isArray());
        assertEquals(2, node.size());
    }

    @Test
    void givenRoundTrip_whenConvertBackAndForth_thenSameStructure() {
        JsonNode original = om.readTree("""
                {
                  "a": 1,
                  "b": [true, 2.5, "x"]
                }
                """);
        Payload payload = PayloadJsonConverter.fromJson(original);
        JsonNode result = PayloadJsonConverter.toJson(payload);

        assertEquals(original, result);
    }
}