package ru.wedwin.aggregator.adapter.in.cli;

import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.output.WriterId;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.domain.model.output.WriteMode;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// todo: work with ApiRegistry and prevent incorrect input
public class ArgsParser {
    public record ParsedArgs(
            boolean interactive,
            RunConfig runConfig
    ) {}

    public record Param(
            ApiId apiId, // todo is it ok to use domain objects inside adapters?
            String param,
            String value
    ) {}

    private final Map<ApiId, ApiParams> paramsByApi;

    public ArgsParser() {
        paramsByApi = new HashMap<>();
    }

    // TODO: help, print output, custom exceptions
    public ParsedArgs parse(String[] args) {

        Path outputPath = null;
        WriterId writerId = null;
        WriteMode writeMode = WriteMode.NEW;

        boolean isInteractive = false;

        labelInteractive:
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--interactive":
                    isInteractive = true;
                    break labelInteractive;
                case "--format": {
                    String value = requireValue(args, ++i, arg);
                    writerId = new WriterId(value);
                    continue;
                }
                case "--mode": {
                    String value = requireValue(args, ++i, arg);
                    writeMode = parseMode(value);
                    continue;
                }
                case "--path": {
                    String value = requireValue(args, ++i, arg);
                    outputPath = Path.of(value);
                    continue;
                }
                case "--apis": {
                    Set<String> apiIdsRaw = requireOneOrMultipleValues(args, ++i, arg);
                    for (String rawId : apiIdsRaw) {
                        paramsByApi.put(new ApiId(rawId), ApiParams.of());
                    }
                    continue;
                }
                case "--params": {
                    Set<String> paramsRaw = requireOneOrMultipleValues(args, ++i, arg);
                    for (String rawParam : paramsRaw) {
                        Param parsedParam = parseParam(rawParam);
                        if (!paramsByApi.containsKey(parsedParam.apiId)) { // todo apiId() ?
                            throw new RuntimeException("passed param for API that not exist");
                        } // todo нужно требовать передачи параметров после передачи списка api, иначе будет ошибка
                        ApiParams params = paramsByApi.get(parsedParam.apiId);
                        params.put(parsedParam.param, parsedParam.value); // todo better?
                    }
                }
            }
        }

        if (outputPath == null) {
            outputPath = Path.of("out." + writerId);
        }

        return new ParsedArgs(
                isInteractive,
                new RunConfig(paramsByApi, new OutputSpec(outputPath, writerId, writeMode)));
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

    private static Set<String> requireOneOrMultipleValues(String[] args, int idx, String flag) {
        if (idx < 0 || idx >= args.length) {
            throw new IllegalArgumentException("incorrect values for flag: " + flag);
        }
        Set<String> values = new HashSet<>();
        while (idx < args.length && !args[idx].startsWith("-")) {
            String value = args[idx];
            if (value.startsWith("-")) {
                throw new IllegalArgumentException("incorrect values for flag: " + flag);
            }
            values.add(value);
            idx++;
        }
        return values;
    }

    private static WriteMode parseMode(String rawMode) {
        String normalizedMode = rawMode.trim().toLowerCase();
        return switch (normalizedMode) {
            case "new" -> WriteMode.NEW;
            case "append" -> WriteMode.APPEND;
            default -> throw new IllegalArgumentException("format is incorrect: " + rawMode);
        };
    }

    private static Param parseParam(String rawData) {
        // todo: add structure check via regex
        String[] separated = rawData.split("[.=]");
        if (separated.length != 3) {
            throw new RuntimeException("params format is incorrect!");
        }
        return new Param(new ApiId(separated[0]), separated[1], separated[2]);
    }

}
