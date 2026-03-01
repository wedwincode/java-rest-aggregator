package ru.wedwin.aggregator.adapter.in.cli;

import ru.wedwin.aggregator.app.registry.WriterRegistry;
import ru.wedwin.aggregator.domain.model.api.ApiDefinition;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.api.ParamSpec;
import ru.wedwin.aggregator.domain.model.in.RunRequest;
import ru.wedwin.aggregator.domain.model.out.OutputFormat;
import ru.wedwin.aggregator.domain.model.out.OutputSpec;
import ru.wedwin.aggregator.domain.model.out.WriteMode;
import ru.wedwin.aggregator.port.in.ApiCatalog;
import ru.wedwin.aggregator.port.out.OutputWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InteractiveMenu {
    private final ApiCatalog apiCatalog;
    private final WriterRegistry writerRegistry;
    private final ConsoleIO io;
    private final Map<ApiId, ApiParams> paramsByApi;

    public InteractiveMenu(ApiCatalog apiCatalog, WriterRegistry writerRegistry, ConsoleIO io) {
        this.apiCatalog = apiCatalog;
        this.writerRegistry = writerRegistry;
        this.io = io;
        this.paramsByApi = new HashMap<>();
    }

    public RunRequest getRunRequest() throws IOException { // todo: wrap with try/catch
        try {
            io.println("Available APIs:");
            io.println("id, name, url");
            for (ApiDefinition d : apiCatalog.list()) {
                io.println(apiInfo(d));
            }
            String rawIds = io.readLine("Enter desired API ids (e.g. api1 api2): "); // todo check if we enter more apis than exist
            Set<ApiId> ids = parseIds(rawIds);
            for (ApiId id : ids) {
                ApiDefinition client = apiCatalog.getDefinition(id);

                io.println("Available query params for " + id + ":");
                for (ParamSpec param : client.supportedParams()) {
                    io.println(param);
                }
                String params = io.readLine("Enter desired params (e.g. param1=123 param2=456) for " + id + ": ");
                ApiParams parsedParams = parseParams(params);
                paramsByApi.put(id, parsedParams);
            }
            io.println("Available formats:");
            io.println(writerRegistry);
            String format = io.readLine("Enter output format: ");
            OutputWriter writer = writerRegistry.require(format);

            io.println("Available write modes:");
            io.println(modeInfo());
            String rawMode = io.readLine("Enter output mode: ").toUpperCase();
            WriteMode mode = WriteMode.valueOf(rawMode);
            String rawPath = io.readLine("Enter output path: ");
            Path path = Path.of(rawPath);
            return new RunRequest(
                    paramsByApi, new OutputSpec(path, OutputFormat.valueOf(format.toUpperCase()), mode) // todo: without OutputFormat enum
            );
        } catch (Exception e) {
            io.println("Error: " + e.getMessage() + ". Try again.");
            throw new IOException("Menu error: ", e); // todo custom
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
        if (string == null || string.isEmpty()) {
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
