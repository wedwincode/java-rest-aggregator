package ru.wedwin.aggregator.adapter.out.viewer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.output.OutputSpec;
import ru.wedwin.aggregator.domain.result.AggregatedItem;
import ru.wedwin.aggregator.domain.result.exception.ResultViewException;
import ru.wedwin.aggregator.port.out.Codec;
import ru.wedwin.aggregator.app.codec.CodecRegistry;
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
    private static final Logger log = LogManager.getLogger(ConsoleResultViewer.class);
    private final CodecRegistry registry;

    public ConsoleResultViewer(CodecRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void all(OutputSpec spec) throws ResultViewException {
        Path path = spec.path();
        try {
            if (!Files.exists(path) || Files.size(path) == 0) {
                return;
            }

            Files.copy(path, System.out);
        } catch (IOException e) {
            throw new ResultViewException("failed to print file " + path, e);
        }
    }

    @Override
    public void byApi(OutputSpec spec, ApiId apiId) throws ResultViewException {
        Codec codec = registry.get(spec.codecId());
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
            throw new ResultViewException("failed to print filtered output for api " + apiId + " from " + path, e);
        }
    }

    @Override
    public void progress(ApiId apiId) {
        System.out.println("Got response for API: " + apiId);
    }

    @Override
    public void error(Throwable error) {
        log.error(error);
        System.out.println("Error: " + error.getMessage());
    }
}
