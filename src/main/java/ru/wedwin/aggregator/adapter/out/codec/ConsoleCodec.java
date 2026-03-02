package ru.wedwin.aggregator.adapter.out.codec;

import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.codec.CodecId;
import ru.wedwin.aggregator.port.out.Codec;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

public class ConsoleCodec implements Codec {
    @Override
    public CodecId id() {
        return new CodecId("console");
    }

    @Override
    public List<AggregatedItem> read(Reader r) {
        return List.of();
    }

    @Override
    public void write(List<AggregatedItem> items, Writer w) {

    }
}
