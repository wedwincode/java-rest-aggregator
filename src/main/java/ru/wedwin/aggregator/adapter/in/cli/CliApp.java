package ru.wedwin.aggregator.adapter.in.cli;

import ru.wedwin.aggregator.app.registry.WriterRegistry;
import ru.wedwin.aggregator.domain.model.in.RunRequest;
import ru.wedwin.aggregator.port.in.ApiCatalog;
import ru.wedwin.aggregator.port.in.RunRequestRetriever;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class CliApp implements RunRequestRetriever {
    private final String[] args;
    private final ApiCatalog apiCatalog;
    private final WriterRegistry writerRegistry;
    private final InputStream in;
    private final PrintStream out;

    public CliApp(String[] args, ApiCatalog apiCatalog, WriterRegistry writerRegistry, InputStream in, PrintStream out) {
        this.args = args;
        this.apiCatalog = apiCatalog;
        this.writerRegistry = writerRegistry;
        this.in = in;
        this.out = out;
    }

    public CliApp(String[] args, ApiCatalog apiCatalog, WriterRegistry writerRegistry) {
        this(args, apiCatalog, writerRegistry, System.in, System.out);
    }

    @Override
    public RunRequest getRunRequest() throws IOException {
        ArgsParser parser = new ArgsParser();
        ArgsParser.ParsedArgs parsedArgs = parser.parse(this.args);
        if (parsedArgs.interactive()) {
            InteractiveMenu menu = new InteractiveMenu(apiCatalog, writerRegistry, new ConsoleIO(in, out));
            return menu.getRunRequest();
        }
        return parsedArgs.runRequest();
    }
}
