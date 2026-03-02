package ru.wedwin.aggregator.app.service;

import ru.wedwin.aggregator.domain.model.codec.CodecId;
import ru.wedwin.aggregator.port.in.CodecCatalog;
import ru.wedwin.aggregator.port.out.Codec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CodecRegistry implements CodecCatalog, CodecProvider {
    private final Map<CodecId, Codec> byId;

    public CodecRegistry(List<Codec> codecs) {
        byId = codecs.stream().collect(Collectors.toMap(Codec::id, c -> c));
    }

    public List<CodecId> list() {
        return new ArrayList<>(byId.keySet());
    }

    public Codec getCodec(CodecId id) {
        Codec codec = byId.get(id);
        if (codec == null) {
            throw new IllegalArgumentException("unknown format: " + id);
        }
        return codec;
    }

}
