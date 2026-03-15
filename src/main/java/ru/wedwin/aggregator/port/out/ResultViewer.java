package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.config.OutputSpec;

public interface ResultViewer {
    void all(OutputSpec spec);
    void byApi(OutputSpec spec, ApiId apiId);
    void progress(ApiId apiId);
    void error(Throwable error);
}
