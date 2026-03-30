package ru.wedwin.aggregator.domain.config;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.config.exception.InvalidExecutionSpecException;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExecutionSpecTest {

    private static final int VALID_TASKS = 2;
    private static final Duration VALID_POLL = Duration.ofSeconds(5);
    private static final Duration VALID_DURATION = Duration.ofSeconds(10);

    @Test
    void givenValidValues_whenCreateExecutionSpec_thenFieldsAreSet() {
        ExecutionSpec spec = new ExecutionSpec(VALID_TASKS, VALID_POLL, VALID_DURATION);

        assertEquals(VALID_TASKS, spec.maxConcurrentTasks());
        assertEquals(VALID_POLL, spec.pollInterval());
        assertEquals(VALID_DURATION, spec.duration());
    }

    @Test
    void givenLessThanOneTask_whenCreateExecutionSpec_thenThrowsException() {
        assertThrows(InvalidExecutionSpecException.class,
                () -> new ExecutionSpec(0, VALID_POLL, VALID_DURATION));
    }

    @Test
    void givenNullPollInterval_whenCreateExecutionSpec_thenThrowsException() {
        assertThrows(InvalidExecutionSpecException.class,
                () -> new ExecutionSpec(VALID_TASKS, null, VALID_DURATION));
    }

    @Test
    void givenNullDuration_whenCreateExecutionSpec_thenThrowsException() {
        assertThrows(InvalidExecutionSpecException.class,
                () -> new ExecutionSpec(VALID_TASKS, VALID_POLL, null));
    }

    @Test
    void givenNegativeOrZeroPollInterval_whenCreateExecutionSpec_thenThrowsException() {
        assertThrows(InvalidExecutionSpecException.class,
                () -> new ExecutionSpec(VALID_TASKS, Duration.ofSeconds(-1), VALID_DURATION));
        assertThrows(InvalidExecutionSpecException.class,
                () -> new ExecutionSpec(VALID_TASKS, Duration.ZERO, VALID_DURATION));
    }

    @Test
    void givenNegativeOrZeroDuration_whenCreateExecutionSpec_thenThrowsException() {
        assertThrows(InvalidExecutionSpecException.class,
                () -> new ExecutionSpec(VALID_TASKS, VALID_POLL, Duration.ofSeconds(-1)));
        assertThrows(InvalidExecutionSpecException.class,
                () -> new ExecutionSpec(VALID_TASKS, VALID_POLL, Duration.ZERO));
    }
}
