package ru.wedwin.aggregator.app.model.job;

import java.util.UUID;

public record JobId(UUID value) {
    public static JobId newId() { // todo everywhere
        return new JobId(UUID.randomUUID());
    }
}
