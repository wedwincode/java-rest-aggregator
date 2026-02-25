package ru.wedwin.aggregator.port.in;

import ru.wedwin.aggregator.domain.model.RunRequest;

public interface RunRequestRetriever {
    RunRequest getRunRequest();
}
