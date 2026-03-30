package ru.wedwin.aggregator.adapter.out.common;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.result.Payload;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PayloadFlattenerTest {

    @Test
    void givenPrimitivePayload_whenFlatten_thenReturnsSingleEntry() {
        Payload payload = new Payload.PInt(42);
        Map<String, String> result = PayloadFlattener.flatten(payload);

        assertEquals(Map.of("payload", "42"), result);
    }

    @Test
    void givenObjectPayload_whenFlatten_thenFlattensFields() {
        Payload payload = new Payload.PObject(Map.of(
                "a", new Payload.PInt(1),
                "b", new Payload.PString("test")
        ));
        Map<String, String> result = PayloadFlattener.flatten(payload);

        assertEquals("1", result.get("payload.a"));
        assertEquals("test", result.get("payload.b"));
    }

    @Test
    void givenNestedObject_whenFlatten_thenBuildsNestedPaths() {
        Payload payload = new Payload.PObject(Map.of(
                "a", new Payload.PObject(Map.of(
                        "b", new Payload.PInt(2)
                ))
        ));
        Map<String, String> result = PayloadFlattener.flatten(payload);

        assertEquals("2", result.get("payload.a.b"));
    }

    @Test
    void givenArray_whenFlatten_thenStoresJsonString() {
        Payload payload = new Payload.PArray(
                java.util.List.of(new Payload.PInt(1), new Payload.PInt(2))
        );
        Map<String, String> result = PayloadFlattener.flatten(payload);

        assertTrue(result.get("payload").contains("["));
    }

    @Test
    void givenNullPayload_whenFlatten_thenReturnsEmptyString() {
        Map<String, String> result = PayloadFlattener.flatten(null);
        assertEquals("", result.get("payload"));
    }

    @Test
    void givenFlatMap_whenUnflatten_thenReturnsObject() {
        Map<String, String> flat = Map.of(
                "payload.a", "1",
                "payload.b", "test"
        );
        Payload result = PayloadFlattener.unflatten(flat);

        assertInstanceOf(Payload.PObject.class, result);

        Payload.PObject obj = (Payload.PObject) result;

        assertEquals(1, ((Payload.PInt) obj.fields().get("a")).value());
        assertEquals("test", ((Payload.PString) obj.fields().get("b")).value());
    }

    @Test
    void givenSingleValue_whenUnflatten_thenReturnsLeaf() {
        Map<String, String> flat = Map.of("payload", "42");
        Payload result = PayloadFlattener.unflatten(flat);

        assertInstanceOf(Payload.PInt.class, result);
        assertEquals(42, ((Payload.PInt) result).value());
    }

    @Test
    void givenBoolean_whenUnflatten_thenParsesBoolean() {
        Payload result = PayloadFlattener.unflatten(Map.of("payload", "true"));

        assertInstanceOf(Payload.PBool.class, result);
        assertTrue(((Payload.PBool) result).value());
    }

    @Test
    void givenDouble_whenUnflatten_thenParsesDouble() {
        Payload result = PayloadFlattener.unflatten(Map.of("payload", "1.5"));

        assertInstanceOf(Payload.PDouble.class, result);
        assertEquals(1.5, ((Payload.PDouble) result).value());
    }

    @Test
    void givenString_whenUnflatten_thenReturnsString() {
        Payload result = PayloadFlattener.unflatten(Map.of("payload", "hello"));

        assertInstanceOf(Payload.PString.class, result);
        assertEquals("hello", ((Payload.PString) result).value());
    }

    @Test
    void givenEmptyMap_whenUnflatten_thenReturnsNullPayload() {
        Payload result = PayloadFlattener.unflatten(Map.of());
        assertInstanceOf(Payload.PNull.class, result);
    }

    @Test
    void givenJsonArrayString_whenUnflatten_thenParsesArray() {
        String json = "[1,2,3]";
        Payload result = PayloadFlattener.unflatten(Map.of("payload", json));

        assertInstanceOf(Payload.PArray.class, result);
    }
}