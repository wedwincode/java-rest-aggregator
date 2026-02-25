package ru.wedwin.aggregator.adapter.in.cli;

import ru.wedwin.aggregator.domain.model.RunRequest;
import ru.wedwin.aggregator.port.in.RunRequestRetriever;

public class CliApp implements RunRequestRetriever {
    private final String[] args;

    public CliApp(String[] args) {
        this.args = args;
    }

    @Override
    public RunRequest getRunRequest() {
        ArgsParser parser = new ArgsParser();
        ArgsParser.ParsedArgs parsedArgs = parser.parse(this.args);
        if (parsedArgs.interactive()) {
            // interactive menu
        }
        return parsedArgs.runRequest();
    }
}
