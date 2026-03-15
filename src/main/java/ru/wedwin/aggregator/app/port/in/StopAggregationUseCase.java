package ru.wedwin.aggregator.app.port.in;

import ru.wedwin.aggregator.app.session.Session;

public interface StopAggregationUseCase {
    void stop(Session session);
}
