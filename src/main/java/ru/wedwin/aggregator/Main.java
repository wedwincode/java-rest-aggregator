package ru.wedwin.aggregator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.wedwin.aggregator.adapter.in.cli.ArgsParseException;
import ru.wedwin.aggregator.adapter.in.cli.CliApp;
import ru.wedwin.aggregator.adapter.out.api.newsapi.NewsApiClient;
import ru.wedwin.aggregator.adapter.out.api.thenewsapi.TheNewsApiClient;
import ru.wedwin.aggregator.adapter.out.api.weatherapi.WeatherApiClient;
import ru.wedwin.aggregator.adapter.out.codec.CsvCodec;
import ru.wedwin.aggregator.adapter.out.codec.JsonCodec;
import ru.wedwin.aggregator.adapter.out.executor.OkHttpExecutor;
import ru.wedwin.aggregator.adapter.out.runner.ScheduledAggregationRunner;
import ru.wedwin.aggregator.adapter.out.saver.FileResultSaver;
import ru.wedwin.aggregator.adapter.out.viewer.ConsoleResultViewer;
import ru.wedwin.aggregator.app.AggregationUseCase;
import ru.wedwin.aggregator.app.service.api.ApiRegistry;
import ru.wedwin.aggregator.app.service.api.ApiRegistryImpl;
import ru.wedwin.aggregator.app.service.codec.CodecRegistryImpl;
import ru.wedwin.aggregator.domain.model.api.exception.ApiResponseException;
import ru.wedwin.aggregator.domain.model.result.exception.ResultSaveException;
import ru.wedwin.aggregator.domain.model.result.exception.ResultViewException;

import java.util.List;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);
    // todo check why max concurrent not working and tasks are executed very fast
    public static void main(String[] args) {
        ApiRegistry apiRegistry = new ApiRegistryImpl(List.of(
                new NewsApiClient(),
                new TheNewsApiClient(),
                new WeatherApiClient()
        ));
        CodecRegistryImpl codecRegistry = new CodecRegistryImpl(List.of(
                new JsonCodec(),
                new CsvCodec()
        ));
        CliApp app = new CliApp(
                args,
                apiRegistry,
                codecRegistry,
                System.in,
                System.out,
                new AggregationUseCase(
                        new FileResultSaver(codecRegistry),
                        new ConsoleResultViewer(codecRegistry),
                        new ScheduledAggregationRunner(
                                apiRegistry,
                                new OkHttpExecutor()
                        )
                )
        );

        try {
            app.run();
        } catch (ArgsParseException e) {
            System.out.println("Error parsing args: " + e.getMessage());
            log.error("error parsing args", e);
            System.exit(1);
        } catch (ApiResponseException e) {
            System.out.println("Error handling api response: " + e.getMessage());
            log.error("error handling api response", e);
            System.exit(2);
        } catch (ResultSaveException e) {
            System.out.println("Error during result saving: " + e.getMessage());
            log.error("error during result saving", e);
            System.exit(3);
        } catch (ResultViewException e) {
            System.out.println("Error during result presentation: " + e.getMessage());
            log.error("error during result presentation", e);
            System.exit(4);
        } catch (Exception e) {
            System.out.println("Unknown error: " + e.getMessage());
            log.error("unknown error", e);
            System.exit(5);
        }
    }
}
