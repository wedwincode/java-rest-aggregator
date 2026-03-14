package ru.wedwin.aggregator.adapter.in.cli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.wedwin.aggregator.app.api.ApiRegistry;
import ru.wedwin.aggregator.app.session.Session;
import ru.wedwin.aggregator.domain.config.RunConfig;
import ru.wedwin.aggregator.app.codec.CodecRegistry;
import ru.wedwin.aggregator.port.in.StartAggregationUseCase;
import ru.wedwin.aggregator.port.in.StopAggregationUseCase;
import ru.wedwin.aggregator.port.in.ViewResultsUseCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;
public class CliApp {
    private static final Logger log = LogManager.getLogger(CliApp.class);
    private final String[] args;
    private final ApiRegistry apiRegistry;
    private final CodecRegistry codecRegistry;
    private final InputStream in;
    private final PrintStream out;
    private final StartAggregationUseCase startAggregationUseCase;
    private final StopAggregationUseCase stopAggregationUseCase;
    private final ViewResultsUseCase viewResultsUseCase;

    private boolean isInteractive = false;

    public CliApp(
            String[] args,
            ApiRegistry apiRegistry,
            CodecRegistry codecRegistry,
            InputStream in,
            PrintStream out,
            StartAggregationUseCase startAggregationUseCase,
            StopAggregationUseCase stopAggregationUseCase,
            ViewResultsUseCase viewResultsUseCase
    ) {
        this.args = args;
        this.apiRegistry = apiRegistry;
        this.codecRegistry = codecRegistry;
        this.in = in;
        this.out = out;
        this.startAggregationUseCase = startAggregationUseCase;
        this.stopAggregationUseCase = stopAggregationUseCase;
        this.viewResultsUseCase = viewResultsUseCase;
    }

    public void run() {
        RunConfig runConfig = getRunConfig();
        Session session = startAggregationUseCase.start(runConfig);

        try {
            waitForCompletion(runConfig);
        } finally {
            stopAggregationUseCase.stop(session);
        }

        viewResultsUseCase.view(runConfig);
    }

    private void waitForCompletion(RunConfig runConfig) {
        Instant deadline = Instant.now().plus(runConfig.executionSpec().duration());

        if (!isInteractive) {
            try {
                while (Instant.now().isBefore(deadline)) {
                    sleep(Duration.ofMillis(200));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        AtomicBoolean stopRequested = new AtomicBoolean(false); // todo read about atomic
        Thread consoleThread = startConsoleWatcher(stopRequested);

        try {
            while (Instant.now().isBefore(deadline) && !stopRequested.get()) {
                sleep(Duration.ofMillis(200));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            consoleThread.interrupt();
        }
    }

    private Thread startConsoleWatcher(AtomicBoolean stopRequested) {
        return Thread.ofVirtual().start(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                while (!Thread.currentThread().isInterrupted()) {
                    String line = reader.readLine();
                    if (line == null) {
                        return;
                    }

                    String command = line.trim();
                    if ("stop".equalsIgnoreCase(command)) {
                        stopRequested.set(true);
                        return;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private RunConfig getRunConfig() {
        try {
            if (args == null) {
                throw new ArgsParseException("args are null");
            }

            if (Arrays.asList(args).contains("--interactive")) {
                isInteractive = true;
                return new InteractiveRunConfigProvider(
                        apiRegistry,
                        codecRegistry,
                        new ConsoleIO(in, out)
                ).getRunConfig();
            }

            return new ArgsRunConfigProvider(args, apiRegistry).getRunConfig();
        } catch (ArgsParseException e) {
            log.error("arguments error: {}", e.getMessage());
            throw e;
        }
    }
}
