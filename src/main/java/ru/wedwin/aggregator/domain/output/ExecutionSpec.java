package ru.wedwin.aggregator.domain.output;

import ru.wedwin.aggregator.domain.output.exception.InvalidExecutionSpecException;

import java.time.Duration;

public record ExecutionSpec (
        int maxConcurrentTasks,
        Duration pollInterval,
        Duration duration
){

    public ExecutionSpec {
        if (maxConcurrentTasks < 1) {
            throw new InvalidExecutionSpecException("number of concurrent tasks should be more than 0");
        }
        if (pollInterval == null) {
            throw new InvalidExecutionSpecException("pollInterval is null");
        }
        if (duration == null) {
            throw new InvalidExecutionSpecException("duration is null");
        }
        if (pollInterval.isNegative() || pollInterval.isZero()) {
            throw new InvalidExecutionSpecException("pollInterval should be greater than 0");
        }
        if (duration.isNegative() || duration.isZero()) {
            throw new InvalidExecutionSpecException("duration should be greater than 0");
        }
    }
}
