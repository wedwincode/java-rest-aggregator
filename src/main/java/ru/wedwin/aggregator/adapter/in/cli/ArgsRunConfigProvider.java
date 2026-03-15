package ru.wedwin.aggregator.adapter.in.cli;

import ru.wedwin.aggregator.app.api.ApiRegistry;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.api.ApiParams;
import ru.wedwin.aggregator.domain.api.ParamMeta;
import ru.wedwin.aggregator.domain.api.exception.InvalidApiIdException;
import ru.wedwin.aggregator.domain.config.RunConfig;
import ru.wedwin.aggregator.domain.config.DisplayMode;
import ru.wedwin.aggregator.domain.config.DisplaySpec;
import ru.wedwin.aggregator.domain.config.ExecutionSpec;
import ru.wedwin.aggregator.domain.config.OutputSpec;
import ru.wedwin.aggregator.domain.config.WriteMode;
import ru.wedwin.aggregator.domain.codec.CodecId;
import ru.wedwin.aggregator.domain.codec.exception.InvalidCodecIdException;

import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ArgsRunConfigProvider {

    private final String[] args;
    private final ApiRegistry registry;
    private final Map<ApiId, ApiParams> queryParamsByApi;
    private final Set<ApiId> selectedApis;
    private final Map<ApiId, Map<String, String>> rawParamsByApi;
    private Path outputPath = null;
    private CodecId codecId = null;
    private WriteMode writeMode = null;
    private int maxConcurrent = 1;
    private Duration pollInterval = null;
    private Duration duration = null;

    private record ParsedValues(Set<String> values, int nextIndex) {}

    public ArgsRunConfigProvider(String[] args, ApiRegistry registry) {
        this.args = args;
        this.registry = registry;
        this.queryParamsByApi = new HashMap<>();
        this.selectedApis = new HashSet<>();
        this.rawParamsByApi = new HashMap<>();
    }

    public RunConfig getRunConfig() {
        parseArgs();

        return validateAndBuild();
    }

    private void parseArgs() {
        int i = 0;
        while (i < args.length) {
            String arg = args[i];

            switch (arg) {
                case "--format" -> i = parseFormat(i);
                case "--mode" -> i = parseMode(i);
                case "--path" -> i = parsePath(i);
                case "--apis" -> i = parseApis(i);
                case "--params" -> i = parseParams(i);
                case "--max-concurrent" -> i = parseMaxConcurrent(i);
                case "--interval" -> i = parseInterval(i);
                case "--duration" -> i = parseDuration(i);
                default -> i += 1;
            }
        }
    }

    private int parseFormat(int i) {
        String value = requireValue(args, i + 1, "--format");

        try {
            codecId = new CodecId(value);
        } catch (InvalidCodecIdException e) {
            throw new ArgsParseException("invalid --format value: " + value, e);
        }

        return i + 2;
    }

    private int parseMode(int i) {
        String value = requireValue(args, i + 1, "--mode");
        writeMode = getMode(value);

        return i + 2;
    }

    private int parsePath(int i) {
        String value = requireValue(args, i + 1, "--path");

        try {
            outputPath = Path.of(value);
        } catch (RuntimeException e) {
            throw new ArgsParseException("invalid --path value: " + value, e);
        }

        return i + 2;
    }

    private int parseApis(int i) {
        ParsedValues pv = requireOneOrMultipleValues(args, i + 1, "--apis");
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

        return pv.nextIndex();
    }

    private int parseParams(int i) {
        ParsedValues pv = requireOneOrMultipleValues(args, i + 1, "--params");
        pv.values().forEach(this::addParam);

        return pv.nextIndex();
    }

    private int parseMaxConcurrent(int i) {
        maxConcurrent = parseIntValue(i, "--max-concurrent");
        return i + 2;
    }

    private int parseInterval(int i) {
        pollInterval = Duration.ofSeconds(parseIntValue(i, "--interval"));
        return i + 2;
    }

    private int parseDuration(int i) {
        duration = Duration.ofSeconds(parseIntValue(i, "--duration"));
        return i + 2;
    }

    private int parseIntValue(int i, String flag) {
        String value = requireValue(args, i + 1, flag);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ArgsParseException("invalid " + flag + " value: " + value, e);
        }
    }

    private void validateRequiredFlags() {
        if (codecId == null) {
            throw new ArgsParseException("format was not specified");
        }
        if (writeMode == null) {
            throw new ArgsParseException("mode was not specified");
        }
    }

    private void fillDefaults() {
        if (outputPath == null) {
            outputPath = Path.of("out." + codecId);
        }
    }

    private RunConfig validateAndBuild() {
        validateRequiredFlags();
        fillDefaults();
        Set<ApiId> allApis = collectAllApis();
        validateApisExist(allApis);
        validateAndBuildParams(allApis);

        try {
            return new RunConfig(
                    queryParamsByApi,
                    new OutputSpec(outputPath, codecId, writeMode),
                    new ExecutionSpec(maxConcurrent, pollInterval, duration),
                    new DisplaySpec(DisplayMode.NONE)
            );
        } catch (RuntimeException e) {
            throw new ArgsParseException("invalid run config", e);
        }
    }

    private Set<ApiId> collectAllApis() {
        Set<ApiId> allApis = new HashSet<>();
        allApis.addAll(selectedApis);
        allApis.addAll(rawParamsByApi.keySet());

        if (allApis.isEmpty()) {
            throw new ArgsParseException("specify at least one api");
        }

        return allApis;
    }

    private void validateApisExist(Set<ApiId> allApis) {
        for (ApiId id: allApis) {
            if (!registry.contains(id)) {
                throw new ArgsParseException("api not exist: " + id);
            }
        }
    }

    private void validateAndBuildParams(Set<ApiId> allApis) {
        for (ApiId id: allApis) {
            Set<String> supportedParams = registry.getDefinition(id).supportedParams()
                    .stream()
                    .map(ParamMeta::key)
                    .collect(Collectors.toSet());

            Map<String, String> params = rawParamsByApi.getOrDefault(id, Map.of());

            for (String key: params.keySet()) {
                if (!supportedParams.contains(key)) {
                    throw new ArgsParseException("unsupported param: " + key + " for api " + id);
                }
            }

            queryParamsByApi.put(id, ApiParams.of(params));
        }
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

    private static WriteMode getMode(String rawMode) {
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
