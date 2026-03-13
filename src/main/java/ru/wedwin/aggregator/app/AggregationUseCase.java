package ru.wedwin.aggregator.app;

import ru.wedwin.aggregator.app.service.session.Session;
import ru.wedwin.aggregator.domain.model.codec.CodecId;
import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.output.WriteMode;
import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.result.exception.ResultSaveException;
import ru.wedwin.aggregator.domain.model.result.exception.ResultViewException;
import ru.wedwin.aggregator.port.in.StartAggregation;
import ru.wedwin.aggregator.port.in.StopAggregation;
import ru.wedwin.aggregator.port.in.ViewResults;
import ru.wedwin.aggregator.port.out.AggregationRunner;
import ru.wedwin.aggregator.port.out.ResultSaver;
import ru.wedwin.aggregator.port.out.ResultViewer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AggregationUseCase implements StartAggregation, StopAggregation, ViewResults {
    private final ResultSaver saver;
    private final ResultViewer viewer;
    private final AggregationRunner runner;
    private final List<AggregatedItem> results;

    public AggregationUseCase(
            ResultSaver saver,
            ResultViewer viewer,
            AggregationRunner runner
    ) {
        this.saver = saver;
        this.viewer = viewer;
        this.runner = runner;
        this.results = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public Session start(RunConfig runConfig) {
        // todo phaser?
        return runner.start(
                runConfig,
                item -> handleResult(
                        item,
                        runConfig.outputSpec().path(),
                        runConfig.outputSpec().codecId()
                ),
                viewer::error);
    }

    private void handleResult(AggregatedItem item, Path path, CodecId codecId) {
        viewer.progress(item.apiId());
        results.add(item);
        try {
            saver.save(new OutputSpec(path, codecId, WriteMode.NEW), results);
        } catch (ResultSaveException e) {
            // todo logs
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop(Session handle) {
        runner.stop(handle);
    }

    @Override
    public void view(RunConfig runConfig) throws ResultViewException {
        switch (runConfig.displaySpec().mode()) {
            case NONE -> {}
            case ALL -> viewer.all(runConfig.outputSpec());
            case BY_API -> viewer.byApi(runConfig.outputSpec(), runConfig.displaySpec().apiId());
        }
    }
}
