package ru.wedwin.aggregator.adapter.out.storage;

import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.port.out.Formatter;
import ru.wedwin.aggregator.port.out.FormatterProvider;
import ru.wedwin.aggregator.port.out.ResultStorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileResultStorage implements ResultStorage {
    private final FormatterProvider provider;

    public FileResultStorage(FormatterProvider provider) {
        this.provider = provider;
    }

    @Override
    public void save(OutputSpec spec, List<AggregatedItem> items) {
        Formatter formatter = provider.getFormatter(spec.formatterId());
        try {
            if (spec.path().getParent() != null) {
                Files.createDirectories(spec.path().getParent()); // todo нужно ли
            }
            switch (spec.mode()) {
                case NEW -> saveNew(spec.path(), items, formatter);
                case APPEND -> saveAppend(spec.path(), items, formatter);
                case null, default -> throw new RuntimeException("null!!"); // todo everywhere
            }
        } catch (IOException e) {
            throw new RuntimeException("failed to write output to " + spec.path(), e);
        }
    }

    @Override
    public void printAll(OutputSpec spec, OutputStream out) {
        Path path = spec.path();
        try {
            if (!Files.exists(path) || Files.size(path) == 0) {
                return;
            }
            Files.copy(path, out);
        } catch (IOException e) {
            throw new RuntimeException("failed to print file " + path, e);
        }
    }

    @Override
    public void printByApi(OutputSpec spec, ApiId apiId, OutputStream out) {
        Formatter formatter = provider.getFormatter(spec.formatterId());
        Path path = spec.path();
        try {
            if (!Files.exists(path) || Files.size(path) == 0) {
                return;
            }
            List<AggregatedItem> items;
            try (BufferedReader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                items = formatter.read(r);
            }

            List<AggregatedItem> filtered = items.stream()
                    .filter(i -> i.apiId().equals(apiId))
                    .toList();

            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
            formatter.write(filtered, w);
        } catch (IOException e) {
            throw new RuntimeException("failed to print filtered output for api " + apiId + " from " + path, e);
        }
    }

    private void saveNew(Path path, List<AggregatedItem> items, Formatter formatter) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(
                path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            formatter.write(items, w);
        }
    }

    private void saveAppend(Path path, List<AggregatedItem> newItems, Formatter formatter) throws IOException {
        List<AggregatedItem> existing = List.of();
        if (Files.exists(path) && Files.size(path) > 0) {
            try (BufferedReader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                existing = formatter.read(r);
            }
        }
        List<AggregatedItem> merged = new ArrayList<>(existing.size() + newItems.size());
        merged.addAll(existing);
        merged.addAll(newItems);
        saveNew(path, merged, formatter);
    }
}
