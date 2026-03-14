package ru.wedwin.aggregator.adapter.in.cli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.wedwin.aggregator.app.AggregationUseCase;
import ru.wedwin.aggregator.app.service.api.ApiRegistry;
import ru.wedwin.aggregator.app.service.session.Session;
import ru.wedwin.aggregator.domain.model.api.exception.ApiResponseException;
import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.app.service.codec.CodecRegistry;
import ru.wedwin.aggregator.domain.model.result.exception.ResultSaveException;
import ru.wedwin.aggregator.domain.model.result.exception.ResultViewException;

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
// todo problem: CliApp is in the 'in' adapters but there's no port
public class CliApp {
    private static final Logger log = LogManager.getLogger(CliApp.class);
    private final String[] args;
    private final ApiRegistry apiRegistry;
    private final CodecRegistry codecRegistry;
    private final InputStream in;
    private final PrintStream out;
    private final AggregationUseCase useCase;

    private boolean isInteractive = false;

    public CliApp(
            String[] args,
            ApiRegistry apiRegistry,
            CodecRegistry codecRegistry,
            InputStream in,
            PrintStream out,
            AggregationUseCase useCase
    ) {
        this.args = args;
        this.apiRegistry = apiRegistry;
        this.codecRegistry = codecRegistry;
        this.in = in;
        this.out = out;
        this.useCase = useCase;
    }

    public void run() throws ApiResponseException, ResultSaveException, ResultViewException {
        // todo: delete resultviewer and print here
        RunConfig runConfig = getRunConfig();
        Session session = useCase.start(runConfig);

        try {
            waitForCompletion(runConfig);
        } finally {
            useCase.stop(session);
        }

        useCase.view(runConfig);
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
