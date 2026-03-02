package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.exception.executor.ExecutorException;

import java.net.URL;

public interface Executor {
    String execute(URL url) throws ExecutorException;
}
