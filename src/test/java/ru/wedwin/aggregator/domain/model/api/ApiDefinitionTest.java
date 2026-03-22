package ru.wedwin.aggregator.domain.model.api;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.api.ApiDefinition;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.api.ParamMeta;
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
// todo refactor
    // todo is it necessary to make test class' fields private?

    static final String idValue = "api1";
    static final ApiId id = new ApiId(idValue);
    static final String urlValue = "https://google.com/";
    static final URL url = buildUrl();
    static final String displayName = "myApi123";
    static final ParamMeta[] paramsRaw = new ParamMeta[]{
            new ParamMeta("key1", true, "default", "desc"),
            new ParamMeta("key2", false, "string", "1234"),
    };
    static final List<ParamMeta> supportedParams = List.of(paramsRaw);

    @Test
    void normalizesName() {
        String displayNameUntrimmed = "     myApi123   ";

        ApiDefinition fromFullConstructor = new ApiDefinition(id, url, displayNameUntrimmed, supportedParams);
        ApiDefinition fromCompactConstructor = new ApiDefinition(idValue, urlValue, displayNameUntrimmed, paramsRaw);
        List<ApiDefinition> definitions = List.of(fromFullConstructor, fromCompactConstructor);

        for (ApiDefinition definition: definitions) {
            assertEquals(id, definition.id());
            assertEquals(urlValue, definition.url().toString()); // todo why comparing without string takes so much time
            assertEquals(displayName, definition.displayName());
            assertEquals(supportedParams, definition.supportedParams());
        }
    }

    @Test
    void rejectsNullId() {
        assertThrows(InvalidApiDefinitionException.class, () -> new ApiDefinition(null, url, displayName, supportedParams));
    }

    @Test
    void rejectsNullUrl() {
        assertThrows(InvalidApiDefinitionException.class,
                () -> new ApiDefinition(id, null, displayName, supportedParams));
    }

    @Test
    void rejectsInvalidUrl() { // todo why not count in coverage
        assertThrows(IllegalArgumentException.class, () -> new ApiDefinition(idValue, "aaa", displayName, paramsRaw));
    }

    @Test
    void rejectsNullDisplayName() {
        assertThrows(InvalidApiDefinitionException.class, () -> new ApiDefinition(id, url, null, supportedParams));
    }

    @Test
    void rejectsBlankDisplayName() {
        assertThrows(InvalidApiDefinitionException.class, () -> new ApiDefinition(id, url, "   ", supportedParams));
    }

    @Test
    void rejectsNullSupportedParams() {
        assertThrows(InvalidApiDefinitionException.class, () -> new ApiDefinition(id, url, displayName, null));
    }

    @Test
    void rejectsNullInsideSupportedParams() {
        List<ParamMeta> paramsWithNull = new ArrayList<>(supportedParams);
        paramsWithNull.add(null);

        assertThrows(InvalidApiDefinitionException.class, () -> new ApiDefinition(id, url, displayName, paramsWithNull));
    }

    private static URL buildUrl() {
        try {
            return new URI(urlValue).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

//    @Test
//    void rejectsIncorrectUrl() { // todo how to test private methods
//        String url = "123";
//        assertThrows(MalformedURLException.class,
//                () -> ApiDefinition.toUrl)
//    }
}
