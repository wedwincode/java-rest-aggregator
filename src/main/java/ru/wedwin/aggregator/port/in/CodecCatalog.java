package ru.wedwin.aggregator.port.in;

import ru.wedwin.aggregator.domain.model.codec.CodecId;

import java.util.List;

public interface CodecCatalog {
    List<CodecId> list();
}
