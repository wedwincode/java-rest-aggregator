//package ru.wedwin.aggregator.app.service.session;
//
//import ru.wedwin.aggregator.app.model.job.Job;
//import ru.wedwin.aggregator.app.model.job.JobId;
//import ru.wedwin.aggregator.domain.model.AggregationSessionId;
//
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//
//// todo compare enum vs class
//public enum InMemorySessionRegistry implements SessionRegistry {
//    INSTANCE;
//
//    private final ConcurrentMap<JobId, Job> sessions; // todo read about concurrent collections
//
//    InMemorySessionRegistry() {
//        sessions = new ConcurrentHashMap<>();
//    }
//
//    @Override
//    public void put(AggregationSessionId sessionId) {
//        sessions.put(job.id(), job);
//    }
//
//    @Override
//    public Job get(JobId id) {
//        return sessions.get(id);
//    }
//
//    @Override
//    public void remove(JobId id) {
//        sessions.remove(id);
//    }
//}
