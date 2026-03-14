package ru.wedwin.aggregator.app.codec;

import ru.wedwin.aggregator.domain.codec.CodecId;
import ru.wedwin.aggregator.port.out.Codec;

import java.util.List;

public interface CodecRegistry {
    void put(Codec codec);
    Codec get(CodecId id);
    List<CodecId> list();
}
