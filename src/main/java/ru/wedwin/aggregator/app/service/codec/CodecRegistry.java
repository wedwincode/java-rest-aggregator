package ru.wedwin.aggregator.app.service.codec;

import ru.wedwin.aggregator.domain.model.codec.CodecId;
import ru.wedwin.aggregator.port.out.Codec;

import java.util.List;

public interface CodecRegistry {
    Codec getCodec(CodecId id);
    List<CodecId> list();
}
