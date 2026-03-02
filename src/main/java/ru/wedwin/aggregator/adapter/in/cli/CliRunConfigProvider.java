package ru.wedwin.aggregator.adapter.in.cli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.port.in.ApiCatalog;
import ru.wedwin.aggregator.port.in.RunConfigProvider;
import ru.wedwin.aggregator.port.in.CodecCatalog;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class CliRunConfigProvider implements RunConfigProvider {
    private static final Logger log = LogManager.getLogger(CliRunConfigProvider.class);
    private final String[] args;
    private final ApiCatalog apiCatalog;
    private final CodecCatalog codecCatalog;
    private final InputStream in;
    private final PrintStream out;

    public CliRunConfigProvider(String[] args, ApiCatalog apiCatalog, CodecCatalog codecCatalog, InputStream in, PrintStream out) {
        this.args = args;
        this.apiCatalog = apiCatalog;
        this.codecCatalog = codecCatalog;
        this.in = in;
        this.out = out;
    }

    @Override
    public RunConfig getRunConfig() {
        try {
            if (Arrays.asList(args).contains("--interactive")) {
                return new InteractiveRunConfigProvider(
                        apiCatalog,
                        codecCatalog,
                        new ConsoleIO(in, out)
                ).getRunConfig();
            }
            return new ArgsRunConfigProvider(args, apiCatalog).getRunConfig();
        } catch (ArgsParseException e) {
            out.println("arguments error: " + e.getMessage());
            log.error("arguments error: {}", e.getMessage());
            throw e;
        }
    }
}
