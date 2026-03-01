package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.result.ResultSaver;
import ru.wedwin.aggregator.domain.model.result.ResultViewer;

public interface ResultStorage extends ResultSaver, ResultViewer {
}
