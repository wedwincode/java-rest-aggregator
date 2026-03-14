package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.output.OutputSpec;
import ru.wedwin.aggregator.domain.result.exception.ResultViewException;

public interface ResultViewer {
    void all(OutputSpec spec) throws ResultViewException;
    void byApi(OutputSpec spec, ApiId apiId) throws ResultViewException;
    void progress(ApiId apiId);
    void error(Throwable error);
}
