package ru.wedwin.aggregator.app.model.job;

public final class Job { // todo final everywhere
    private final JobId id;

    public Job(JobId id) {
        this.id = id;
    }

    public JobId id() {
        return id;
    }

    public void cancel() {

    }

    public boolean isDone() {
        return false;
    }
}
