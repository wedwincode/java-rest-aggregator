package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;

import java.io.OutputStream;

public interface ResultViewer {
    void all(OutputSpec spec);
    void byApi(OutputSpec spec, ApiId apiId);
}
