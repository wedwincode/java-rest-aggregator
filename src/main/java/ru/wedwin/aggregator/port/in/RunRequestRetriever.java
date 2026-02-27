package ru.wedwin.aggregator.port.in;

import ru.wedwin.aggregator.domain.model.in.RunRequest;

public interface RunRequestRetriever {
    RunRequest getRunRequest();
}
