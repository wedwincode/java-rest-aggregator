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
import ru.wedwin.aggregator.adapter.out.runner.ScheduledRunner;
import ru.wedwin.aggregator.adapter.out.saver.FileResultSaver;
import ru.wedwin.aggregator.adapter.out.viewer.ConsoleResultViewer;
import ru.wedwin.aggregator.app.AggregationService;
import ru.wedwin.aggregator.app.api.ApiRegistry;
import ru.wedwin.aggregator.app.api.ApiRegistryImpl;
import ru.wedwin.aggregator.app.codec.CodecRegistry;
import ru.wedwin.aggregator.app.codec.CodecRegistryImpl;
import ru.wedwin.aggregator.domain.api.exception.ApiResponseException;
import ru.wedwin.aggregator.domain.result.exception.ResultSaveException;
import ru.wedwin.aggregator.domain.result.exception.ResultViewException;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        ApiRegistry apiRegistry = ApiRegistryImpl.INSTANCE;
        apiRegistry.put(new NewsApiClient());
        apiRegistry.put(new TheNewsApiClient());
        apiRegistry.put(new WeatherApiClient());

        CodecRegistry codecRegistry = CodecRegistryImpl.INSTANCE;
        codecRegistry.put(new CsvCodec());
        codecRegistry.put(new JsonCodec());

        AggregationService service = new AggregationService(
                new FileResultSaver(codecRegistry),
                new ConsoleResultViewer(codecRegistry),
                new ScheduledRunner(apiRegistry, new OkHttpExecutor())
        );

        CliApp app = new CliApp(
                args,
                apiRegistry,
                codecRegistry,
                System.in,
                System.out,
                service,
                service,
                service
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
