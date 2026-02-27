package ru.wedwin.aggregator.domain.model;

import java.util.List;
import java.util.Map;
// todo int
public sealed interface Payload permits Payload.PNull, Payload.PBool, Payload.PInt, Payload.PDouble,
                                        Payload.PString, Payload.PArray, Payload.PObject {
    record PNull() implements Payload {}
    record PBool(boolean value) implements Payload {}
    record PInt(int value) implements Payload {}
    record PDouble(double value) implements Payload {}
    record PString(String value) implements Payload {}
    record PArray(List<Payload> items) implements Payload {}
    record PObject(Map<String, Payload> fields) implements Payload {}
}
