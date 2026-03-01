package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.exception.executor.ExecutorException;

public interface Executor {
    String execute(String url) throws ExecutorException;
}
