package ru.wedwin.aggregator.adapter.in.cli;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.app.api.ApiRegistry;
import ru.wedwin.aggregator.domain.api.ApiDefinition;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.api.ParamMeta;
import ru.wedwin.aggregator.domain.config.RunConfig;
import ru.wedwin.aggregator.domain.config.WriteMode;
import ru.wedwin.aggregator.domain.codec.CodecId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArgsRunConfigProviderTest {

    @Test
    void givenValidArgs_whenParse_thenBuildsRunConfig() {
        ApiRegistry registry = mock(ApiRegistry.class);

        ApiId apiId = new ApiId("api1");

        when(registry.contains(apiId)).thenReturn(true);
        when(registry.getDefinition(apiId)).thenReturn(new ApiDefinition(
                "api1",
                "https://example.com",
                "test",
                new ParamMeta("q", false, null, "")
        ));

        String[] args = {
                "--format", "json",
                "--mode", "new",
                "--apis", "api1",
                "--interval", "1",
                "--duration", "5"
        };

        ArgsRunConfigProvider provider = new ArgsRunConfigProvider(args, registry);

        RunConfig config = provider.getRunConfig();

        assertEquals(new CodecId("json"), config.outputSpec().codecId());
        assertEquals(WriteMode.NEW, config.outputSpec().mode());
        assertTrue(config.queryParamsByApi().containsKey(apiId));
    }

    @Test
    void givenMissingFormat_whenParse_thenThrows() {
        ApiRegistry registry = mock(ApiRegistry.class);

        String[] args = {
                "--mode", "new",
                "--apis", "api1"
        };

        ArgsRunConfigProvider provider = new ArgsRunConfigProvider(args, registry);

        assertThrows(ArgsParseException.class, provider::getRunConfig);
    }

    @Test
    void givenInvalidApi_whenParse_thenThrows() {
        ApiRegistry registry = mock(ApiRegistry.class);

        ApiId apiId = new ApiId("api1");
        when(registry.contains(apiId)).thenReturn(false);

        String[] args = {
                "--format", "json",
                "--mode", "new",
                "--apis", "api1"
        };

        ArgsRunConfigProvider provider = new ArgsRunConfigProvider(args, registry);

        assertThrows(ArgsParseException.class, provider::getRunConfig);
    }

    @Test
    void givenParams_whenParse_thenAttachToApi() {
        ApiRegistry registry = mock(ApiRegistry.class);

        ApiId apiId = new ApiId("api1");

        when(registry.contains(apiId)).thenReturn(true);
        when(registry.getDefinition(apiId)).thenReturn(new ApiDefinition(
                "api1",
                "https://example.com",
                "test",
                new ParamMeta("q", false, null, "")
        ));

        String[] args = {
                "--format", "json",
                "--mode", "new",
                "--params", "api1.q=test",
                "--interval", "1",
                "--duration", "5"
        };

        ArgsRunConfigProvider provider = new ArgsRunConfigProvider(args, registry);

        RunConfig config = provider.getRunConfig();

        assertEquals("test", config.queryParamsByApi().get(apiId).asMap().get("q"));
    }

    @Test
    void givenUnsupportedParam_whenParse_thenThrows() {
        ApiRegistry registry = mock(ApiRegistry.class);

        ApiId apiId = new ApiId("api1");

        when(registry.contains(apiId)).thenReturn(true);
        when(registry.getDefinition(apiId)).thenReturn(new ApiDefinition(
                "api1",
                "https://example.com",
                "test",
                new ParamMeta("q", false, null, "")
        ));

        String[] args = {
                "--format", "json",
                "--mode", "new",
                "--params", "api1.bad=value"
        };

        ArgsRunConfigProvider provider = new ArgsRunConfigProvider(args, registry);

        assertThrows(ArgsParseException.class, provider::getRunConfig);
    }

    @Test
    void givenNoApis_whenParse_thenThrows() {
        ApiRegistry registry = mock(ApiRegistry.class);

        String[] args = {
                "--format", "json",
                "--mode", "new"
        };

        ArgsRunConfigProvider provider = new ArgsRunConfigProvider(args, registry);

        assertThrows(ArgsParseException.class, provider::getRunConfig);
    }

    @Test
    void givenNoPath_whenParse_thenUsesDefault() {
        ApiRegistry registry = mock(ApiRegistry.class);

        ApiId apiId = new ApiId("api1");

        when(registry.contains(apiId)).thenReturn(true);
        when(registry.getDefinition(apiId)).thenReturn(new ApiDefinition(
                "api1",
                "https://example.com",
                "test"
        ));

        String[] args = {
                "--format", "json",
                "--mode", "new",
                "--apis", "api1",
                "--interval", "1",
                "--duration", "5"
        };

        ArgsRunConfigProvider provider = new ArgsRunConfigProvider(args, registry);

        RunConfig config = provider.getRunConfig();

        assertEquals("out.json", config.outputSpec().path().toString());
    }
}