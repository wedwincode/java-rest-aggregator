package ru.wedwin.aggregator.app.session;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SessionTest {

    @Test
    void givenDispatchTrigger_whenSet_thenTriggerIsUsedOnStopScheduling() {
        CompletableFuture<?> trigger = new CompletableFuture<>();
        Session session = new Session(1);
        session.setDispatchTrigger(trigger);
        session.stopScheduling();

        assertTrue(trigger.isCancelled());
    }

    @Test
    void givenAvailableSlot_whenTryAcquire_thenReturnsTrue() {
        Session session = new Session(1);
        boolean acquired = session.tryAcquireExecutionSlot();

        assertTrue(acquired);
    }

    @Test
    void givenNoAvailableSlots_whenTryAcquire_thenReturnsFalse() {
        Session session = new Session(1);
        session.tryAcquireExecutionSlot();
        boolean acquired = session.tryAcquireExecutionSlot();

        assertFalse(acquired);
    }

    @Test
    void givenReleasedSlot_whenTryAcquireAgain_thenReturnsTrue() {
        Session session = new Session(1);
        session.tryAcquireExecutionSlot();
        session.releaseExecutionSlot();
        boolean acquired = session.tryAcquireExecutionSlot();

        assertTrue(acquired);
    }

    @Test
    void givenStoppedScheduling_whenTryAcquire_thenReturnsFalse() {
        Session session = new Session(1);
        session.stopScheduling();
        boolean acquired = session.tryAcquireExecutionSlot();

        assertFalse(acquired);
    }

    @Test
    void givenRunningExecution_whenCheckHasRunningExecutions_thenReturnsTrue() {
        Session session = new Session(1);
        session.tryAcquireExecutionSlot();

        assertTrue(session.hasRunningExecutions());
    }

    @Test
    void givenNoRunningExecution_whenCheckHasRunningExecutions_thenReturnsFalse() {
        Session session = new Session(1);
        assertFalse(session.hasRunningExecutions());
    }
}
