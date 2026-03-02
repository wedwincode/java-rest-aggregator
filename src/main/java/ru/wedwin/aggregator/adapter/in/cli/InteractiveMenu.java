package ru.wedwin.aggregator.adapter.in.cli;

import ru.wedwin.aggregator.domain.model.api.ApiDefinition;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.api.ParamMeta;
import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.domain.model.output.DisplayMode;
import ru.wedwin.aggregator.domain.model.output.DisplaySpec;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.output.WriteMode;
import ru.wedwin.aggregator.domain.model.codec.CodecId;
import ru.wedwin.aggregator.port.in.ApiCatalog;
import ru.wedwin.aggregator.port.in.CodecCatalog;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InteractiveMenu {
    private final ApiCatalog apiCatalog;
    private final CodecCatalog codecCatalog;
    private final ConsoleIO io;
    private final Map<ApiId, ApiParams> paramsByApi;

    public InteractiveMenu(ApiCatalog apiCatalog, CodecCatalog codecCatalog, ConsoleIO io) {
        this.apiCatalog = apiCatalog;
        this.codecCatalog = codecCatalog;
        this.io = io;
        this.paramsByApi = new HashMap<>();
    }
    // todo: print result
    public RunConfig getRunRequest() { // todo: wrap with try/catch
        try {
            io.println("Available APIs:");
            io.println("id, name, url");
            for (ApiDefinition d : apiCatalog.list()) {
                io.println(apiInfo(d));
            }
            String rawIds = io.readLine("Enter desired API ids (e.g. api1 api2): ");
            Set<ApiId> ids = parseIds(rawIds);
            for (ApiId id : ids) {
                ApiDefinition client = apiCatalog.getDefinition(id);

                io.println("Available query params for " + id + ":");
                for (ParamMeta param : client.supportedParams()) {
                    io.println(param);
                }
                String params = io.readLine("Enter desired params (e.g. param1=123 param2=456) for " + id + ": ");
                ApiParams parsedParams = parseParams(params);
                paramsByApi.put(id, parsedParams);
            }

            List<CodecId> codecs = codecCatalog.list();
            io.println("Available output formats:");
            io.println(codecs); // todo better
            CodecId codec = new CodecId(io.readLine("Enter output format: "));
            if (!codecs.contains(codec)) {
                throw new IllegalArgumentException("unsupported format: " + codec);
            }

            io.println("Available write modes:");
            io.println(modeInfo());
            String rawMode = io.readLine("Enter output mode: ").toUpperCase();
            WriteMode writeMode = WriteMode.valueOf(rawMode);
            String rawPath = io.readLine("Enter output path: ");
            Path path = Path.of(rawPath);
            String rawPrintDecision = io.readLine("What results do you want to print? (all/none/apiId): ").trim().toLowerCase();
            ApiId apiToDisplay = null;
            DisplayMode displayMode;
            switch (rawPrintDecision) {
                case "all" -> displayMode = DisplayMode.ALL;
                case "none" -> displayMode = DisplayMode.NONE;
                default -> {
                    if (!apiCatalog.contains(new ApiId(rawPrintDecision))) {
                        throw new RuntimeException("unknown id");
                    }
                    displayMode = DisplayMode.BY_API;
                    apiToDisplay = new ApiId(rawPrintDecision);
                }
            }
            io.println();
            return new RunConfig(
                    paramsByApi,
                    new OutputSpec(path, codec, writeMode),
                    new DisplaySpec(apiToDisplay, displayMode)
            );
        } catch (Exception e) {
            io.println("Error: " + e.getMessage() + ". Try again.");
            throw new RuntimeException("Menu error: ", e); // todo custom
        }
    }

    private static String apiInfo(ApiDefinition c) {
        return c.id() + "\t" + c.displayName() + "\t" + c.url();
    }

    private static String modeInfo() {
        return Arrays.stream(WriteMode.values())
                .map(Enum::name)
                .collect(Collectors.joining(" "));
    }

    private Set<ApiId> parseIds(String string) {
        Set<ApiId> allowedIds = apiCatalog.list().stream().map(ApiDefinition::id).collect(Collectors.toSet());
        if (string == null || string.isBlank()) {
            throw new IllegalArgumentException("at least one id must be specified");
        }
        Set<ApiId> ids = new HashSet<>();
        for (String idString: string.split(" ")) {
            ApiId id = new ApiId(idString);
            if (!allowedIds.contains(id)) {
                throw new IllegalArgumentException("id not exist: " + id);
            }
            ids.add(id);
        }
        return ids;
    }

    private static ApiParams parseParams(String string) {
        if (string == null || string.isEmpty()) {
            return ApiParams.of();
        }
        Map<String, String> params = new HashMap<>();
        for (String entry: string.split(" ")) {
            String[] entrySplit = entry.split("="); // todo param checks as in parseIds
            if (entrySplit.length != 2) {
                throw new IllegalArgumentException("param format is incorrect: " + entry);
            }
            params.put(entrySplit[0], entrySplit[1]);
        }
        return ApiParams.of(params);
    }
}
