package ru.wedwin.aggregator.app.codec;

import ru.wedwin.aggregator.domain.codec.CodecId;
import ru.wedwin.aggregator.app.port.out.Codec;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CodecRegistryImpl implements CodecRegistry {

    private final Map<CodecId, Codec> byId;

    public CodecRegistryImpl() {
        this.byId = new LinkedHashMap<>();
    }

    @Override
    public void put(Codec codec) {
        byId.put(codec.id(), codec);
    }

    @Override
    public Codec get(CodecId id) {
        Codec codec = byId.get(id);
        if (codec == null) {
            throw new IllegalArgumentException("unknown format: " + id);
        }
        return codec;
    }

    @Override
    public List<CodecId> list() {
        return new ArrayList<>(byId.keySet());
    }
}
