package ru.wedwin.aggregator.adapter.in.cli;

import ru.wedwin.aggregator.app.api.ApiRegistry;
import ru.wedwin.aggregator.domain.api.ApiDefinition;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.api.ApiParams;
import ru.wedwin.aggregator.domain.api.ParamMeta;
import ru.wedwin.aggregator.domain.config.RunConfig;
import ru.wedwin.aggregator.domain.output.DisplayMode;
import ru.wedwin.aggregator.domain.output.DisplaySpec;
import ru.wedwin.aggregator.domain.output.ExecutionSpec;
import ru.wedwin.aggregator.domain.output.OutputSpec;
import ru.wedwin.aggregator.domain.output.WriteMode;
import ru.wedwin.aggregator.domain.codec.CodecId;
import ru.wedwin.aggregator.app.codec.CodecRegistry;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InteractiveRunConfigProvider {

    private final ApiRegistry apiRegistry;
    private final CodecRegistry codecRegistry;
    private final ConsoleIO io;

    public InteractiveRunConfigProvider(ApiRegistry apiRegistry, CodecRegistry codecRegistry, ConsoleIO io) {
        this.apiRegistry = apiRegistry;
        this.codecRegistry = codecRegistry;
        this.io = io;
    }

    public RunConfig getRunConfig() {
        try {
            List<ApiDefinition> apis = showAndGetApis();
            Set<ApiId> ids = readAndValidateApiIds(apis);
            Map<ApiId, ApiParams> queryParamsByApi = readParamsForApis(ids);
            CodecId codec = readAndValidateCodec();
            OutputSpec outputSpec = readOutputSpec(codec);
            ExecutionSpec executionSpec = readExecutionSpec();
            DisplaySpec displaySpec = readDisplaySpec();
            waitForStartDecision();
            return new RunConfig(queryParamsByApi, outputSpec, executionSpec, displaySpec);
        } catch (ArgsParseException e) {
            throw new ArgsParseException("menu error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ArgsParseException("unknown menu error: " + e.getMessage(), e);
        }
    }

    private List<ApiDefinition> showAndGetApis() {
        io.println("Available APIs:");
        io.println("id, name, url");

        List<ApiDefinition> apiDefinitionList = apiRegistry.list();
        for (ApiDefinition d: apiDefinitionList) {
            io.println(apiInfo(d));
        }

        return apiDefinitionList;
    }

    private Set<ApiId> readAndValidateApiIds(List<ApiDefinition> apis) {
        String rawIds = io.readLine("Enter desired API ids (e.g. api1 api2): ");
        Set<ApiId> ids = parseIds(rawIds);

        Set<ApiId> availableIds = apis.stream()
                .map(ApiDefinition::id)
                .collect(Collectors.toSet());

        for (ApiId id: ids) {
            if (!availableIds.contains(id)) {
                throw new ArgsParseException("api not exist: " + id);
            }
        }

        return ids;
    }

    private Map<ApiId, ApiParams> readParamsForApis(Set<ApiId> ids) {
        Map<ApiId, ApiParams> queryParamsByApi = new LinkedHashMap<>();

        for (ApiId id: ids) {
            ApiDefinition api = apiRegistry.getDefinition(id);
            Map<String, String> parsedParams = readParamsForApi(id, api);
            queryParamsByApi.put(id, ApiParams.of(parsedParams));
        }

        return queryParamsByApi;
    }

    private Map<String, String> readParamsForApi(ApiId id, ApiDefinition api) {
        io.println("Available query params for " + id + ":");
        for (ParamMeta param : api.supportedParams()) {
            io.println(param);
        }

        String paramsRaw = io.readLine("Enter desired params " +
                "(e.g. param1=123 param2=456) for " + id + ": ");
        Map<String, String> parsedParams = parseParams(paramsRaw);

        Set<String> availableParams = api.supportedParams().stream()
                .map(ParamMeta::key)
                .collect(Collectors.toSet());

        for (String key: parsedParams.keySet()) {
            if (!availableParams.contains(key)) {
                throw new ArgsParseException("unsupported param: " + key + " for api " + id);
            }
        }

        return parsedParams;
    }

    private CodecId readAndValidateCodec() {
        List<CodecId> codecs = codecRegistry.list();
        io.println("Available output formats:");
        io.println(codecInfo(codecs));

        String rawId = io.readLine("Enter output format: ").trim();
        if (rawId.isBlank()) {
            throw new ArgsParseException("format is blank");
        }

        CodecId codec = new CodecId(rawId);
        if (!codecs.contains(codec)) {
            throw new ArgsParseException("unsupported format: " + codec);
        }

        return codec;
    }

    private OutputSpec readOutputSpec(CodecId codec) {
        io.println("Available write modes:");
        io.println(modeInfo());

        String rawMode = io.readLine("Enter output mode: ").toUpperCase();
        if (rawMode.isBlank()) {
            throw new ArgsParseException("mode is empty");
        }

        WriteMode writeMode;
        try {
            writeMode = WriteMode.valueOf(rawMode);
        } catch (IllegalArgumentException e) {
            throw new ArgsParseException("writeMode not exist: " + rawMode);
        }

        String rawPath = io.readLine("Enter output path: ");
        if (rawPath.isBlank()) {
            throw new ArgsParseException("path is empty");
        }

        Path path;
        try {
            path = Path.of(rawPath);
        } catch (InvalidPathException e) {
            throw new ArgsParseException("invalid path: " + rawPath);
        }

        return new OutputSpec(path, codec, writeMode);
    }

    private ExecutionSpec readExecutionSpec() {
        String rawMaxConcurrent = io.readLine("Enter max concurrent tasks: ");
        int maxConcurrent;
        try {
            maxConcurrent = Integer.parseInt(rawMaxConcurrent);
        } catch (NumberFormatException e) {
            throw new ArgsParseException("invalid value: " + rawMaxConcurrent, e);
        }

        String rawPollInterval = io.readLine("Enter poll interval (s): ");
        Duration pollInterval;
        try {
            pollInterval = Duration.ofSeconds(Integer.parseInt(rawPollInterval));
        } catch (NumberFormatException e) {
            throw new ArgsParseException("invalid value: " + rawPollInterval, e);
        }

        String rawDuration = io.readLine("Enter duration (s): ");
        Duration duration;
        try {
            duration = Duration.ofSeconds(Integer.parseInt(rawDuration));
        } catch (NumberFormatException e) {
            throw new ArgsParseException("invalid value: " + rawDuration, e);
        }

        return new ExecutionSpec(maxConcurrent, pollInterval, duration);
    }

    private DisplaySpec readDisplaySpec() {
        String rawPrintDecision = io.readLine("What results do you want to print? " +
                "(all/none/apiId): ").trim().toLowerCase();
        ApiId apiToDisplay = null;
        DisplayMode displayMode;

        switch (rawPrintDecision) {
            case "all" -> displayMode = DisplayMode.ALL;
            case "none" -> displayMode = DisplayMode.NONE;
            default -> {
                if (!apiRegistry.contains(new ApiId(rawPrintDecision))) {
                    throw new ArgsParseException("unknown id");
                }

                displayMode = DisplayMode.BY_API;
                apiToDisplay = new ApiId(rawPrintDecision);
            }
        }

        return new DisplaySpec(apiToDisplay, displayMode);
    }

    private void waitForStartDecision() {
        String decision = io.readLine("You're all set to start aggregation. Type 'START' to continue: ");
        if (!decision.trim().equalsIgnoreCase("start")) {
            throw new ArgsParseException("aggregation is interrupted. try again");
        }
        io.println("Type 'STOP' to stop aggregation or wait until it finish");
        io.println();
    }

    private Set<ApiId> parseIds(String string) {
        if (string == null || string.isBlank()) {
            throw new ArgsParseException("at least one id must be specified");
        }

        return Arrays.stream(string.trim().split("\\s+"))
                .map(ApiId::new)
                .collect(Collectors.toSet());
    }

    private Map<String, String> parseParams(String rawData) {
        if (rawData == null || rawData.isEmpty()) {
            return Map.of();
        }

        Map<String, String> params = new HashMap<>();
        for (String s: rawData.trim().split("\\s+")) {
            int eq = s.indexOf('=');
            if (eq <= 0 || eq == s.length() - 1) {
                throw new ArgsParseException("params format is incorrect: expected param=value");
            }

            String keyRaw = s.substring(0, eq);
            String valRaw = s.substring(eq + 1);
            params.put(keyRaw, valRaw);
        }

        return params;
    }

    private static String apiInfo(ApiDefinition c) {
        return c.id() + "\t" + c.displayName() + "\t" + c.url();
    }

    private static String modeInfo() {
        return Arrays.stream(WriteMode.values())
                .map(Enum::name)
                .collect(Collectors.joining(" "));
    }

    private static String codecInfo(List<CodecId> codecs) {
        if (codecs == null || codecs.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (CodecId c: codecs) {
            if (c == null) {
                continue;
            }
            if (!sb.isEmpty()) {
                sb.append(' ');
            }
            sb.append(c);
        }

        return sb.toString();
    }
}
