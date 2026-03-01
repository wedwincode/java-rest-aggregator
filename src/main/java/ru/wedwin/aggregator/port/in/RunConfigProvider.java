package ru.wedwin.aggregator.port.in;

import ru.wedwin.aggregator.domain.model.in.RunConfig;

import java.io.IOException;

public interface RunConfigProvider {
    RunConfig getRunRequest() throws IOException;
}
