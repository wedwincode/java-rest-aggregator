package ru.wedwin.aggregator.port.in;

import ru.wedwin.aggregator.app.service.session.Session;

public interface StopAggregation {
    void stop(Session handle);
}
