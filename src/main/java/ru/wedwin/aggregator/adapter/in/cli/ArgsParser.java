package ru.wedwin.aggregator.adapter.in.cli;

import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.api.ParamMeta;
import ru.wedwin.aggregator.domain.model.output.WriterId;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.domain.model.output.WriteMode;
import ru.wedwin.aggregator.port.in.ApiCatalog;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ArgsParser {
    private final String[] args;
    private final ApiCatalog catalog;
    private final Map<ApiId, ApiParams> apisWithParams;
    private final Set<ApiId> selectedApis;
    private final Map<ApiId, Map<String, String>> rawParamsByApi;

    private record ParsedValues(Set<String> values, int nextIndex) {}

    public ArgsParser(String[] args, ApiCatalog catalog) {
        this.args = args;
        this.catalog = catalog;
        this.apisWithParams = new HashMap<>();
        this.selectedApis = new HashSet<>();
        this.rawParamsByApi = new HashMap<>();
    }

    public boolean isInteractive() {
        return Arrays.asList(args).contains("--interactive");
    }

    // TODO: custom exceptions
    public RunConfig parse() {
        Path outputPath = null;
        WriterId writerId = null;
        WriteMode writeMode = null;

        int i = 0;
        while (i < args.length) {
            String arg = args[i];
            switch (arg) {
                case "--writer" -> {
                    String value = requireValue(args, i + 1, arg);
                    writerId = new WriterId(value);
                    i += 2;
                }
                case "--mode" -> {
                    String value = requireValue(args, i + 1, arg);
                    writeMode = parseMode(value);
                    i += 2;
                }
                case "--path" -> {
                    String value = requireValue(args, i + 1, arg);
                    outputPath = Path.of(value);
                    i += 2;
                }
                case "--apis" -> {
                    ParsedValues pv = requireOneOrMultipleValues(args, i + 1, arg);
                    selectedApis.addAll(pv.values().stream().map(ApiId::new).collect(Collectors.toSet()));
                    i = pv.nextIndex();
                }
                case "--params" -> {
                    ParsedValues pv = requireOneOrMultipleValues(args, i + 1, arg);
                    pv.values().forEach(this::addParam);
                    i = pv.nextIndex();
                }
                default -> i += 1;
            }
        }

        if (writerId == null) {
            throw new IllegalArgumentException("writer was not specified");
        }
        if (writeMode == null) {
            throw new IllegalArgumentException("mode was not specified");
        }
        if (outputPath == null) {
            outputPath = Path.of("out." + writerId);
        }

        Set<ApiId> allApis = new HashSet<>();
        allApis.addAll(selectedApis);
        allApis.addAll(rawParamsByApi.keySet());

        if (allApis.isEmpty()) {
            throw new IllegalArgumentException("specify at least one api");
        }

        allApis.forEach((id) -> {
            if (!catalog.contains(id)) {
                throw new IllegalArgumentException("api not exist: " + id);
            }
            Set<String> supportedParams = catalog.getDefinition(id).supportedParams()
                    .stream().map(ParamMeta::key).collect(Collectors.toSet());
            Map<String, String> params = rawParamsByApi.getOrDefault(id, Map.of());
            params.forEach((key, _) -> {
                if (!supportedParams.contains(key)) {
                    throw new IllegalArgumentException("unsupported param: " + key + " for api " + id);
                }
            });
            apisWithParams.put(id, ApiParams.of(params));
        });

        return new RunConfig(apisWithParams, new OutputSpec(outputPath, writerId, writeMode));
    }

    private static String requireValue(String[] args, int idx, String flag) {
        if (idx < 0 || idx >= args.length) {
            throw new IllegalArgumentException("missing value for: " + flag);
        }
        String value = args[idx];
        if (value.startsWith("-")) {
            throw new IllegalArgumentException("missing value for: " + flag);
        }
        return value;
    }

    private static ParsedValues requireOneOrMultipleValues(String[] args, int idx, String flag) {
        if (idx < 0 || idx >= args.length) {
            throw new IllegalArgumentException("incorrect values for flag: " + flag);
        }
        Set<String> values = new HashSet<>();
        while (idx < args.length && !args[idx].startsWith("-")) {
            String value = args[idx++];
            values.add(value);
        }
        if (values.isEmpty()) {
            throw new IllegalArgumentException("incorrect values for flag: " + flag);
        }

        return new ParsedValues(values, idx);
    }

    private static WriteMode parseMode(String rawMode) {
        if (rawMode == null) {
            throw new IllegalArgumentException("mode is null");
        }
        String normalized = rawMode.trim().toUpperCase();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("mode is empty");
        }
        try {
            return WriteMode.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("mode is incorrect: " + rawMode, e);
        }
    }

    private void addParam(String rawData) {
        // todo: add structure check via regex
        String[] separated = rawData.split("[.=]");
        if (separated.length != 3) {
            throw new RuntimeException("params format is incorrect");
        }
        ApiId id = new ApiId(separated[0]);
        Map<String, String> currentParams = rawParamsByApi.computeIfAbsent(id, _ -> new HashMap<>());
        currentParams.put(separated[1], separated[2]);
    }
}
