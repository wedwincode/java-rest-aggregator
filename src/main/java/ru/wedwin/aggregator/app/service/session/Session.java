package ru.wedwin.aggregator.app.service.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

// todo move outside domain
// todo разобраться
public final class Session {
    private final List<ScheduledFuture<?>> scheduledTasks = Collections.synchronizedList(new ArrayList<>()); // todo is it necessary to use sync
    private final AtomicBoolean acceptingNewTasks = new AtomicBoolean(true); // todo volatile vs atomic?
    private final Semaphore launches;
    private final int maxConcurrentTasks;

    public Session(int maxConcurrentTasks) {
        this.maxConcurrentTasks = maxConcurrentTasks;
        this.launches = new Semaphore(maxConcurrentTasks);
    }

    public void addTask(ScheduledFuture<?> task) {
        scheduledTasks.add(task);
    }

    public boolean tryAcquireLaunch() {
        if (!acceptingNewTasks.get()) { // fast check if the system is not accepting tasks at all
            return false;
        }

        boolean acquired = launches.tryAcquire();
        if (!acquired) {
            return false;
        }

        if (!acceptingNewTasks.get()) {
            launches.release();
            return false;
        }

        return true;
    }

    public void completeLaunch() {
        launches.release();
    }

    public void stopScheduling() {
        acceptingNewTasks.set(false);
        synchronized (scheduledTasks) {
            for (ScheduledFuture<?> task: scheduledTasks) {
                task.cancel(false);
            }
        }
    }

    public boolean hasInFlightTasks() {
        return launches.availablePermits() < maxConcurrentTasks;
    }
}