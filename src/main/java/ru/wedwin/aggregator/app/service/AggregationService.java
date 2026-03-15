package ru.wedwin.aggregator.app.service;

import ru.wedwin.aggregator.app.session.Session;
import ru.wedwin.aggregator.domain.codec.CodecId;
import ru.wedwin.aggregator.domain.config.RunConfig;
import ru.wedwin.aggregator.domain.config.OutputSpec;
import ru.wedwin.aggregator.domain.config.WriteMode;
import ru.wedwin.aggregator.domain.result.AggregatedItem;
import ru.wedwin.aggregator.app.port.in.StartAggregationUseCase;
import ru.wedwin.aggregator.app.port.in.StopAggregationUseCase;
import ru.wedwin.aggregator.app.port.in.ViewResultsUseCase;
import ru.wedwin.aggregator.app.port.out.Runner;
import ru.wedwin.aggregator.app.port.out.ResultSaver;
import ru.wedwin.aggregator.app.port.out.ResultViewer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AggregationService implements StartAggregationUseCase, StopAggregationUseCase, ViewResultsUseCase {
    private final ResultSaver saver;
    private final ResultViewer viewer;
    private final Runner runner;
    private final List<AggregatedItem> results;

    public AggregationService(
            ResultSaver saver,
            ResultViewer viewer,
            Runner runner
    ) {
        this.saver = saver;
        this.viewer = viewer;
        this.runner = runner;
        this.results = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public Session start(RunConfig runConfig) {
        return runner.start(
                runConfig,
                item -> handleResult(
                        item,
                        runConfig.outputSpec().path(),
                        runConfig.outputSpec().codecId()),
                viewer::error);
    }

    private void handleResult(AggregatedItem item, Path path, CodecId codecId) {
        viewer.progress(item.apiId());
        results.add(item);
        saver.save(new OutputSpec(path, codecId, WriteMode.NEW), results);
    }

    @Override
    public void stop(Session session) {
        runner.stop(session);
    }

    @Override
    public void view(RunConfig runConfig) {
        switch (runConfig.displaySpec().mode()) {
            case NONE -> {}
            case ALL -> viewer.all(runConfig.outputSpec());
            case BY_API -> viewer.byApi(runConfig.outputSpec(), runConfig.displaySpec().apiId());
        }
    }
}
