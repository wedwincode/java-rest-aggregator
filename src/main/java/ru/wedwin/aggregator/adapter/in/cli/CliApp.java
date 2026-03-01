package ru.wedwin.aggregator.adapter.in.cli;

import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.port.in.ApiCatalog;
import ru.wedwin.aggregator.port.in.RunConfigProvider;
import ru.wedwin.aggregator.port.in.WriterCatalog;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class CliApp implements RunConfigProvider {
    private final String[] args;
    private final ApiCatalog apiCatalog;
    private final WriterCatalog writerCatalog;
    private final InputStream in;
    private final PrintStream out;

    public CliApp(String[] args, ApiCatalog apiCatalog, WriterCatalog writerCatalog, InputStream in, PrintStream out) {
        this.args = args;
        this.apiCatalog = apiCatalog;
        this.writerCatalog = writerCatalog;
        this.in = in;
        this.out = out;
    }

    @Override
    public RunConfig getRunConfig() throws IOException {
        ArgsParser parser = new ArgsParser(args, apiCatalog);
        if (parser.isInteractive()) {
            InteractiveMenu menu = new InteractiveMenu(apiCatalog, writerCatalog, new ConsoleIO(in, out));
            return menu.getRunRequest();
        }
        return parser.parse();
    }
}
