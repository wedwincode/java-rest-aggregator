package ru.wedwin.aggregator.app.service;

import ru.wedwin.aggregator.domain.model.codec.CodecId;
import ru.wedwin.aggregator.port.out.Codec;

public interface CodecProvider {
    Codec getCodec(CodecId id);
}
