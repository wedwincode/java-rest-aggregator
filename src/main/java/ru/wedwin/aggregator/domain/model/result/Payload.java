package ru.wedwin.aggregator.domain.model.result;

import ru.wedwin.aggregator.domain.model.result.exception.InvalidPayloadException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public sealed interface Payload permits Payload.PNull, Payload.PBool, Payload.PInt, Payload.PDouble,
                                        Payload.PString, Payload.PArray, Payload.PObject {
    record PNull() implements Payload {}
    record PBool(boolean value) implements Payload {}
    record PInt(int value) implements Payload {}
    record PDouble(double value) implements Payload {}
    record PString(String value) implements Payload {
        public PString {
            if (value == null) {
                throw new InvalidPayloadException("string value is null");
            }
        }
    }
    record PArray(List<Payload> items) implements Payload {
        public PArray {
            if (items == null) {
                throw new InvalidPayloadException("array items is null");
            }
            if (items.stream().anyMatch(Objects::isNull)) {
                throw new InvalidPayloadException("array contains null item");
            }
//            items = List.copyOf(items);
        }
    }
    record PObject(Map<String, Payload> fields) implements Payload {
        public PObject {
            if (fields == null) {
                throw new InvalidPayloadException("object fields is null");
            }
            if (fields.containsKey(null)) {
                throw new InvalidPayloadException("object contains null key");
            }
            if (fields.containsValue(null)) {
                throw new InvalidPayloadException("object contains null value");
            }
//            fields = Map.copyOf(fields);
        }
    }
}
