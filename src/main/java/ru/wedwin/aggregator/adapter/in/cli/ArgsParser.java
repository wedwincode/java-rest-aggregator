package ru.wedwin.aggregator.adapter.in.cli;

import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.api.ParamMeta;
import ru.wedwin.aggregator.domain.model.api.exception.InvalidApiIdException;
import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.domain.model.output.DisplayMode;
import ru.wedwin.aggregator.domain.model.output.DisplaySpec;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.output.WriteMode;
import ru.wedwin.aggregator.domain.model.format.FormatterId;
import ru.wedwin.aggregator.domain.model.format.exception.InvalidFormatterIdException;
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
    private Path outputPath = null;
    private FormatterId formatterId = null;
    private WriteMode writeMode = null;

    private record ParsedValues(Set<String> values, int nextIndex) {}

    public ArgsParser(String[] args, ApiCatalog catalog) {
        this.args = args;
        this.catalog = catalog;
        this.apisWithParams = new HashMap<>();
        this.selectedApis = new HashSet<>();
        this.rawParamsByApi = new HashMap<>();
    }

    private static String requireValue(String[] args, int idx, String flag) {
        if (idx < 0 || idx >= args.length) {
            throw new ArgsParseException("missing value for: " + flag);
        }
        String value = args[idx];
        if (value.startsWith("-")) {
            throw new ArgsParseException("missing value for: " + flag);
        }
        return value;
    }

    private static ParsedValues requireOneOrMultipleValues(String[] args, int idx, String flag) {
        if (idx < 0 || idx >= args.length) {
            throw new ArgsParseException("incorrect values for flag: " + flag);
        }
        Set<String> values = new HashSet<>();
        while (idx < args.length && !args[idx].startsWith("-")) {
            String value = args[idx++];
            values.add(value);
        }
        if (values.isEmpty()) {
            throw new ArgsParseException("incorrect values for flag: " + flag);
        }

        return new ParsedValues(values, idx);
    }

    private static WriteMode parseMode(String rawMode) {
        if (rawMode == null) {
            throw new ArgsParseException("mode is null");
        }
        String normalized = rawMode.trim().toUpperCase();
        if (normalized.isEmpty()) {
            throw new ArgsParseException("mode is empty");
        }
        try {
            return WriteMode.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new ArgsParseException("mode is incorrect: " + rawMode, e);
        }
    }

    public boolean isInteractive() {
        return Arrays.asList(args).contains("--interactive");
    }

    public RunConfig parse() {
        int i = 0;
        while (i < args.length) {
            String arg = args[i];
            switch (arg) {
                case "--format" -> {
                    String value = requireValue(args, i + 1, arg);
                    try {
                        formatterId = new FormatterId(value);
                    } catch (InvalidFormatterIdException e) {
                        throw new ArgsParseException("invalid --format value: " + value, e);
                    }
                    i += 2;
                }
                case "--mode" -> {
                    String value = requireValue(args, i + 1, arg);
                    writeMode = parseMode(value);
                    i += 2;
                }
                case "--path" -> {
                    String value = requireValue(args, i + 1, arg);
                    try {
                        outputPath = Path.of(value);
                    } catch (RuntimeException e) {
                        throw new ArgsParseException("invalid --path value: " + value, e);
                    }
                    i += 2;
                }
                case "--apis" -> {
                    ParsedValues pv = requireOneOrMultipleValues(args, i + 1, arg);
                    Set<ApiId> set = new HashSet<>();
                    for (String s : pv.values()) {
                        try {
                            ApiId id = new ApiId(s);
                            set.add(id);
                        } catch (InvalidApiIdException e) {
                            throw new ArgsParseException("invalid --apis value: " + s, e);
                        }
                    }
                    selectedApis.addAll(set);
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

        return validate();
    }

    private RunConfig validate() {
        if (formatterId == null) {
            throw new ArgsParseException("format was not specified");
        }
        if (writeMode == null) {
            throw new ArgsParseException("mode was not specified");
        }
        if (outputPath == null) {
            outputPath = Path.of("out." + formatterId);
        }

        Set<ApiId> allApis = new HashSet<>();
        allApis.addAll(selectedApis);
        allApis.addAll(rawParamsByApi.keySet());

        if (allApis.isEmpty()) {
            throw new ArgsParseException("specify at least one api");
        }

        allApis.forEach((id) -> {
            if (!catalog.contains(id)) {
                throw new ArgsParseException("api not exist: " + id);
            }
            Set<String> supportedParams = catalog.getDefinition(id).supportedParams()
                    .stream().map(ParamMeta::key).collect(Collectors.toSet());
            Map<String, String> params = rawParamsByApi.getOrDefault(id, Map.of());
            params.forEach((key, _) -> {
                if (!supportedParams.contains(key)) {
                    throw new ArgsParseException("unsupported param: " + key + " for api " + id);
                }
            });
            apisWithParams.put(id, ApiParams.of(params));
        });

        try {
            return new RunConfig(
                    apisWithParams,
                    new OutputSpec(outputPath, formatterId, writeMode),
                    new DisplaySpec(DisplayMode.NONE) // todo validation for this shit
            );
        } catch (RuntimeException e) {
            throw new ArgsParseException("invalid run config", e);
        }
    }

    private void addParam(String rawData) {
        if (rawData == null || rawData.isBlank()) {
            throw new ArgsParseException("params format is incorrect: empty value");
        }
        String s = rawData.trim();
        if (s.contains(" ")) {
            throw new ArgsParseException("params format is incorrect: spaces are not allowed: " + rawData);
        }
        int dot = s.indexOf('.');
        int eq = s.indexOf('=');

        if (dot < 0 || eq < 0) {
            throw new ArgsParseException("params format is incorrect: expected api.param=value");
        }
        if (dot == 0 || eq <= dot + 1 || eq == s.length() - 1) {
            throw new ArgsParseException("params format is incorrect: expected api.param=value");
        }
        if (s.indexOf('.', dot + 1) != -1) {
            throw new ArgsParseException("params format is incorrect: too many '.' in " + rawData);
        }
        if (s.indexOf('=', eq + 1) != -1) {
            throw new ArgsParseException("params format is incorrect: too many '=' in " + rawData);
        }

        String apiRaw = s.substring(0, dot);
        String keyRaw = s.substring(dot + 1, eq);
        String valRaw = s.substring(eq + 1);

        try {
            ApiId id = new ApiId(apiRaw);
            Map<String, String> currentParams = rawParamsByApi.computeIfAbsent(id, _ -> new HashMap<>());
            currentParams.put(keyRaw, valRaw);
        } catch (InvalidApiIdException e) {
            throw new ArgsParseException("invalid param api id: " + apiRaw, e);
        }
    }
}
