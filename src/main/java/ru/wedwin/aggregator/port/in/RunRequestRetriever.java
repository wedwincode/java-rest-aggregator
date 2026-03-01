package ru.wedwin.aggregator.port.in;

import ru.wedwin.aggregator.domain.model.in.RunRequest;

import java.io.IOException;

public interface RunRequestRetriever {
    RunRequest getRunRequest() throws IOException;
}
