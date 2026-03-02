package ru.wedwin.aggregator.adapter.out.viewer;

import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.port.out.Codec;
import ru.wedwin.aggregator.port.out.CodecProvider;
import ru.wedwin.aggregator.port.out.ResultViewer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConsoleResultViewer implements ResultViewer {
    private final CodecProvider provider;

    public ConsoleResultViewer(CodecProvider provider) {
        this.provider = provider;
    }

    @Override
    public void all(OutputSpec spec) {
        Path path = spec.path();
        try {
            if (!Files.exists(path) || Files.size(path) == 0) {
                return;
            }
            Files.copy(path, System.out);
        } catch (IOException e) {
            throw new RuntimeException("failed to print file " + path, e);
        }
    }

    @Override
    public void byApi(OutputSpec spec, ApiId apiId) {
        Codec codec = provider.getCodec(spec.codecId());
        Path path = spec.path();
        try {
            if (!Files.exists(path) || Files.size(path) == 0) {
                return;
            }
            List<AggregatedItem> items;
            try (BufferedReader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                items = codec.read(r);
            }

            List<AggregatedItem> filtered = items.stream()
                    .filter(i -> i.apiId().equals(apiId))
                    .toList();

            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
            codec.write(filtered, w);
        } catch (IOException e) {
            throw new RuntimeException("failed to print filtered output for api " + apiId + " from " + path, e);
        }
    }
}
