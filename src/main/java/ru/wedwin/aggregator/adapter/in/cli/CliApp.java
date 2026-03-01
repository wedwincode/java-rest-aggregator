package ru.wedwin.aggregator.adapter.in.cli;

import ru.wedwin.aggregator.app.registry.ApiRegistry;
import ru.wedwin.aggregator.app.registry.WriterRegistry;
import ru.wedwin.aggregator.domain.model.in.RunRequest;
import ru.wedwin.aggregator.port.in.RunRequestRetriever;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class CliApp implements RunRequestRetriever {
    private final String[] args;
    private final ApiRegistry apiRegistry;
    private final WriterRegistry writerRegistry;
    private final InputStream in;
    private final PrintStream out;

    public CliApp(String[] args, ApiRegistry apiRegistry, WriterRegistry writerRegistry, InputStream in, PrintStream out) {
        this.args = args;
        this.apiRegistry = apiRegistry;
        this.writerRegistry = writerRegistry;
        this.in = in;
        this.out = out;
    }

    public CliApp(String[] args, ApiRegistry apiRegistry, WriterRegistry writerRegistry) {
        this(args, apiRegistry, writerRegistry, System.in, System.out);
    }

    @Override
    public RunRequest getRunRequest() throws IOException {
        ArgsParser parser = new ArgsParser();
        ArgsParser.ParsedArgs parsedArgs = parser.parse(this.args);
        if (parsedArgs.interactive()) {
            InteractiveMenu menu = new InteractiveMenu(apiRegistry, writerRegistry, new ConsoleIO(in, out));
            return menu.getRunRequest();
        }
        return parsedArgs.runRequest();
    }
}
