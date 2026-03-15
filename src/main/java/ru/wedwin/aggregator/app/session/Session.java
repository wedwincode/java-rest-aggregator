package ru.wedwin.aggregator.app.session;

import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

public final class Session {

    private final int maxConcurrentTasks;
    private final Semaphore executionSlots;

    private Future<?> dispatchTrigger;
    private volatile boolean acceptingNewTasks = true;

    public Session(int maxConcurrentTasks) {
        this.maxConcurrentTasks = maxConcurrentTasks;
        this.executionSlots = new Semaphore(maxConcurrentTasks);
    }

    public void setDispatchTrigger(Future<?> trigger) {
        dispatchTrigger = trigger;
    }

    public boolean tryAcquireExecutionSlot() {
        if (!acceptingNewTasks) { // fast check if the system is not accepting tasks at all
            return false;
        }

        boolean acquired = executionSlots.tryAcquire();
        if (!acquired) {
            return false;
        }

        if (!acceptingNewTasks) {
            executionSlots.release();
            return false;
        }

        return true;
    }

    public void releaseExecutionSlot() {
        executionSlots.release();
    }

    public void stopScheduling() {
        acceptingNewTasks = false;

        if (dispatchTrigger != null) {
            dispatchTrigger.cancel(false);
        }
    }

    public boolean hasRunningExecutions() {
        return executionSlots.availablePermits() < maxConcurrentTasks;
    }
}