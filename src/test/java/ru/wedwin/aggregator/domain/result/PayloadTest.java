package ru.wedwin.aggregator.domain.result;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.result.exception.InvalidPayloadException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PayloadTest {

    @Test
    void givenNoValue_whenCreatePNull_thenInstanceIsCreated() {
        Payload.PNull payload = new Payload.PNull();
        assertEquals(Payload.PNull.class, payload.getClass());
    }

    @Test
    void givenBooleanValue_whenCreatePBool_thenValueIsSet() {
        Payload.PBool payload = new Payload.PBool(true);
        assertTrue(payload.value());
    }

    @Test
    void givenIntValue_whenCreatePInt_thenValueIsSet() {
        Payload.PInt payload = new Payload.PInt(1);
        assertEquals(1, payload.value());
    }

    @Test
    void givenDoubleValue_whenCreatePDouble_thenValueIsSet() {
        Payload.PDouble payload = new Payload.PDouble(3D);
        assertEquals(3D, payload.value());
    }

    @Test
    void givenValidString_whenCreatePString_thenValueIsSet() {
        Payload.PString payload = new Payload.PString("aaa");
        assertEquals("aaa", payload.value());
    }

    @Test
    void givenNullString_whenCreatePString_thenThrowsException() {
        assertThrows(InvalidPayloadException.class, () -> new Payload.PString(null));
    }

    @Test
    void givenValidItems_whenCreatePArray_thenItemsAreSet() {
        List<Payload> items = List.of(new Payload.PBool(false));
        Payload.PArray payload = new Payload.PArray(items);

        assertEquals(items, payload.items());
    }

    @Test
    void givenNullItems_whenCreatePArray_thenThrowsException() {
        assertThrows(InvalidPayloadException.class, () -> new Payload.PArray(null));
    }

    @Test
    void givenItemsContainingNull_whenCreatePArray_thenThrowsException() {
        List<Payload> items = new ArrayList<>();
        items.add(new Payload.PBool(false));
        items.add(null);

        assertThrows(InvalidPayloadException.class, () -> new Payload.PArray(items));
    }

    @Test
    void givenValidFields_whenCreatePObject_thenFieldsAreSet() {
        Map<String, Payload> fields = Map.of("field", new Payload.PBool(false));
        Payload.PObject payload = new Payload.PObject(fields);

        assertEquals(fields, payload.fields());
    }

    @Test
    void givenNullFields_whenCreatePObject_thenThrowsException() {
        assertThrows(InvalidPayloadException.class, () -> new Payload.PObject(null));
    }

    @Test
    void givenFieldsContainingNullKey_whenCreatePObject_thenThrowsException() {
        Map<String, Payload> fields = new HashMap<>();
        fields.put("field", new Payload.PBool(false));
        fields.put(null, new Payload.PBool(true));

        assertThrows(InvalidPayloadException.class, () -> new Payload.PObject(fields));
    }

    @Test
    void givenFieldsContainingNullValue_whenCreatePObject_thenThrowsException() {
        Map<String, Payload> fields = new HashMap<>();
        fields.put("field", new Payload.PBool(false));
        fields.put("another", null);

        assertThrows(InvalidPayloadException.class, () -> new Payload.PObject(fields));
    }
}
