package ru.wedwin.aggregator.domain.model.result;

import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;

import java.io.OutputStream;

// todo: change "formatter" name to "codec"
public interface ResultViewer {
    void printAll(OutputSpec spec, OutputStream out);
    void printByApi(OutputSpec spec, ApiId apiId, OutputStream out);
}
