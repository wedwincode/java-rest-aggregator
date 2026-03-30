package ru.wedwin.aggregator.adapter.in.cli;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.app.api.ApiRegistry;
import ru.wedwin.aggregator.app.codec.CodecRegistry;
import ru.wedwin.aggregator.domain.api.ApiDefinition;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.api.ParamMeta;
import ru.wedwin.aggregator.domain.codec.CodecId;
import ru.wedwin.aggregator.domain.config.RunConfig;
import ru.wedwin.aggregator.domain.config.WriteMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InteractiveRunConfigProviderTest {

    @Test
    void givenValidInput_whenRun_thenBuildsConfig() {
        ApiRegistry apiRegistry = mock(ApiRegistry.class);
        CodecRegistry codecRegistry = mock(CodecRegistry.class);
        ApiId apiId = new ApiId("api1");

        when(apiRegistry.list()).thenReturn(List.of(
                new ApiDefinition(
                        "api1",
                        "https://example.com",
                        "test",
                        new ParamMeta("q", false, null, "")
                )
        ));

        when(apiRegistry.contains(apiId)).thenReturn(true);
        when(apiRegistry.getDefinition(apiId)).thenReturn(
                new ApiDefinition(
                        "api1",
                        "https://example.com",
                        "test",
                        new ParamMeta("q", false, null, "")
                )
        );

        when(codecRegistry.list()).thenReturn(List.of(new CodecId("json")));

        String input = String.join("\n",
                "api1",
                "q=test",
                "json",
                "new",
                "out.json",
                "1",
                "1",
                "5",
                "all",
                "start"
        ) + "\n";
        ConsoleIO io = new ConsoleIO(
                new ByteArrayInputStream(input.getBytes()),
                new PrintStream(new ByteArrayOutputStream())
        );
        InteractiveRunConfigProvider provider = new InteractiveRunConfigProvider(apiRegistry, codecRegistry, io);
        RunConfig config = provider.getRunConfig();

        assertEquals(new CodecId("json"), config.outputSpec().codecId());
        assertEquals(WriteMode.NEW, config.outputSpec().mode());
        assertTrue(config.queryParamsByApi().containsKey(apiId));
        assertEquals("test", config.queryParamsByApi().get(apiId).asMap().get("q"));
    }

    @Test
    void givenUnknownApi_whenRun_thenThrows() {
        ApiRegistry apiRegistry = mock(ApiRegistry.class);
        CodecRegistry codecRegistry = mock(CodecRegistry.class);

        when(apiRegistry.list()).thenReturn(List.of());

        String input = "badApi\n";
        ConsoleIO io = new ConsoleIO(
                new ByteArrayInputStream(input.getBytes()),
                new PrintStream(new ByteArrayOutputStream())
        );
        InteractiveRunConfigProvider provider = new InteractiveRunConfigProvider(apiRegistry, codecRegistry, io);

        assertThrows(ArgsParseException.class, provider::getRunConfig);
    }

    @Test
    void givenNoStartCommand_whenRun_thenThrows() {
        ApiRegistry apiRegistry = mock(ApiRegistry.class);
        CodecRegistry codecRegistry = mock(CodecRegistry.class);
        ApiId apiId = new ApiId("api1");

        when(apiRegistry.list()).thenReturn(List.of(
                new ApiDefinition("api1", "https://example.com", "test")
        ));

        when(apiRegistry.contains(apiId)).thenReturn(true);
        when(apiRegistry.getDefinition(apiId)).thenReturn(
                new ApiDefinition("api1", "https://example.com", "test")
        );

        when(codecRegistry.list()).thenReturn(List.of(new CodecId("json")));

        String input = String.join("\n",
                "api1",
                "",
                "json",
                "new",
                "out.json",
                "1",
                "1",
                "5",
                "none",
                "nope"
        ) + "\n";
        ConsoleIO io = new ConsoleIO(
                new ByteArrayInputStream(input.getBytes()),
                new PrintStream(new ByteArrayOutputStream())
        );
        InteractiveRunConfigProvider provider = new InteractiveRunConfigProvider(apiRegistry, codecRegistry, io);

        assertThrows(ArgsParseException.class, provider::getRunConfig);
    }
}