package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.result.exception.ResultViewException;

import java.io.OutputStream;

public interface ResultViewer {
    void all(OutputSpec spec) throws ResultViewException;
    void byApi(OutputSpec spec, ApiId apiId) throws ResultViewException;
}
