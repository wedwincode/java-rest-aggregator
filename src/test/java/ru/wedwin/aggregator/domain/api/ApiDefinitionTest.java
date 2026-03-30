package ru.wedwin.aggregator.domain.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.wedwin.aggregator.domain.api.exception.InvalidApiDefinitionException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApiDefinitionTest {

    private static final String ID_VALUE = "api1";
    private static final ApiId ID = new ApiId(ID_VALUE);

    private static final String URL_VALUE = "https://google.com/";
    private static final URL URL = buildUrl();

    private static final String DISPLAY_NAME = "myApi123";
    private static final ParamMeta[] PARAMS_RAW = new ParamMeta[]{
            new ParamMeta("key1", true, "default", "desc"),
            new ParamMeta("key2", false, "string", "1234"),
    };

    private static final List<ParamMeta> SUPPORTED_PARAMS = List.of(PARAMS_RAW);

    @Test
    void givenNonNormalizedDisplayName_whenCreateApiDefinition_thenDisplayNameIsNormalized() {
        String displayNameUntrimmed = "     myApi123   ";

        ApiDefinition fromFullConstructor = new ApiDefinition(ID, URL, displayNameUntrimmed, SUPPORTED_PARAMS);
        ApiDefinition fromCompactConstructor = new ApiDefinition(ID_VALUE, URL_VALUE, displayNameUntrimmed, PARAMS_RAW);

        for (ApiDefinition definition: List.of(fromFullConstructor, fromCompactConstructor)) {
            assertEquals(ID, definition.id());
            // https://docs.oracle.com/javase/8/docs/api/java/net/URL.html <-- comparing is a blocking operation
            assertEquals(URL_VALUE, definition.url().toString());
            assertEquals(DISPLAY_NAME, definition.displayName());
            assertEquals(SUPPORTED_PARAMS, definition.supportedParams());
        }
    }

    @Test
    void givenNullId_whenCreateApiDefinition_thenThrowsException() {
        assertThrows(InvalidApiDefinitionException.class,
                () -> new ApiDefinition(null, URL, DISPLAY_NAME, SUPPORTED_PARAMS));
    }

    @Test
    void givenNullUrl_whenCreateApiDefinition_thenThrowsException() {
        assertThrows(InvalidApiDefinitionException.class,
                () -> new ApiDefinition(ID, null, DISPLAY_NAME, SUPPORTED_PARAMS));
    }

    @Test
    void givenInvalidUrl_whenCreateApiDefinition_thenThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new ApiDefinition(ID_VALUE, "aaa", DISPLAY_NAME, PARAMS_RAW));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    void givenInvalidDisplayName_whenCreateApiDefinition_thenThrowsException(String displayName) {
        assertThrows(InvalidApiDefinitionException.class,
                () -> new ApiDefinition(ID, URL, displayName, SUPPORTED_PARAMS));
    }

    @Test
    void givenNullSupportedParams_whenCreateApiDefinition_thenThrowsException() {
        assertThrows(InvalidApiDefinitionException.class,
                () -> new ApiDefinition(ID, URL, DISPLAY_NAME, null));
    }

    @Test
    void givenSupportedParamsContainingNull_whenCreateApiDefinition_thenThrowsException() {
        List<ParamMeta> paramsWithNull = new ArrayList<>(SUPPORTED_PARAMS);
        paramsWithNull.add(null);

        assertThrows(InvalidApiDefinitionException.class,
                () -> new ApiDefinition(ID, URL, DISPLAY_NAME, paramsWithNull));
    }

    private static URL buildUrl() {
        try {
            return new URI(URL_VALUE).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
