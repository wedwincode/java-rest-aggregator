package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.codec.CodecId;

public interface CodecProvider {
    Codec getCodec(CodecId id);
}
