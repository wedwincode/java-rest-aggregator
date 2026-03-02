package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.codec.CodecId;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

public interface Codec {
    CodecId id();
    List<AggregatedItem> read(Reader r) throws IOException;
    void write(List<AggregatedItem> items, Writer w) throws IOException;
}
