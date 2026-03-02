package ru.wedwin.aggregator.adapter.out.saver;

import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.port.out.Codec;
import ru.wedwin.aggregator.app.service.CodecProvider;
import ru.wedwin.aggregator.port.out.ResultSaver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileResultSaver implements ResultSaver {
    private final CodecProvider provider;

    public FileResultSaver(CodecProvider provider) {
        this.provider = provider;
    }

    @Override
    public void save(OutputSpec spec, List<AggregatedItem> items) {
        Codec codec = provider.getCodec(spec.codecId());
        try {
            if (spec.path().getParent() != null) {
                Files.createDirectories(spec.path().getParent()); // todo нужно ли
            }
            switch (spec.mode()) {
                case NEW -> saveNew(spec.path(), items, codec);
                case APPEND -> saveAppend(spec.path(), items, codec);
                case null, default -> throw new RuntimeException("null!!"); // todo everywhere
            }
        } catch (IOException e) {
            throw new RuntimeException("failed to write output to " + spec.path(), e);
        }
    }

    private void saveNew(Path path, List<AggregatedItem> items, Codec codec) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(
                path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            codec.write(items, w);
        }
    }

    private void saveAppend(Path path, List<AggregatedItem> newItems, Codec codec) throws IOException {
        List<AggregatedItem> existing = List.of();
        if (Files.exists(path) && Files.size(path) > 0) {
            try (BufferedReader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                existing = codec.read(r);
            }
        }
        List<AggregatedItem> merged = new ArrayList<>(existing.size() + newItems.size());
        merged.addAll(existing);
        merged.addAll(newItems);
        saveNew(path, merged, codec);
    }
}
