package ru.wedwin.aggregator.adapter.in.cli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.port.in.ApiCatalog;
import ru.wedwin.aggregator.port.in.RunConfigProvider;
import ru.wedwin.aggregator.port.in.FormatterCatalog;

import java.io.InputStream;
import java.io.PrintStream;

public class CliApp implements RunConfigProvider {
    private static final Logger log = LogManager.getLogger(CliApp.class);
    private final String[] args;
    private final ApiCatalog apiCatalog;
    private final FormatterCatalog formatterCatalog;
    private final InputStream in;
    private final PrintStream out;

    public CliApp(String[] args, ApiCatalog apiCatalog, FormatterCatalog formatterCatalog, InputStream in, PrintStream out) {
        this.args = args;
        this.apiCatalog = apiCatalog;
        this.formatterCatalog = formatterCatalog;
        this.in = in;
        this.out = out;
    }

    @Override
    public RunConfig getRunConfig() {
        ArgsParser parser = new ArgsParser(args, apiCatalog);
        if (parser.isInteractive()) {
            InteractiveMenu menu = new InteractiveMenu(apiCatalog, formatterCatalog, new ConsoleIO(in, out));
            return menu.getRunRequest();
        }
        try {
            return parser.parse();
        } catch (ArgsParseException e) {
            out.println("arguments error: " + e.getMessage());
            log.error("arguments error: {}", e.getMessage());
            throw e;
        }
    }
}
