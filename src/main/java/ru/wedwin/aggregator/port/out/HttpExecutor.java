package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.exceptions.executor.ExecutorException;

public interface HttpExecutor {
    String execute(String url) throws ExecutorException;
}
