package ru.wedwin.aggregator.adapter.out.http;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OkHttpHttpExecutorTest {

    @Test
    void givenSuccessfulResponse_whenExecute_thenReturnsBody() throws Exception {
        OkHttpClient client = mock(OkHttpClient.class);
        Call call = mock(Call.class);

        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(successResponse("{\"ok\":true}"));

        OkHttpHttpExecutor executor = new OkHttpHttpExecutor(client);

        String result = executor.execute(
                new URI("https://example.com/search").toURL(),
                Map.of("q", "java")
        );

        assertEquals("{\"ok\":true}", result);
    }

    @Test
    void givenSuccessfulResponseWithParams_whenExecute_thenBuildsGetRequestWithQueryParams() throws Exception {
        OkHttpClient client = mock(OkHttpClient.class);
        Call call = mock(Call.class);

        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(successResponse("body"));

        OkHttpHttpExecutor executor = new OkHttpHttpExecutor(client);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("q", "java");
        params.put("lang", "ru");
        params.put("blank", "   ");
        params.put("validBlankValue", "");
        params.put("skipNullValue", null);

        executor.execute(new URI("https://example.com/search").toURL(), params);

        verify(client).newCall(argThat(request -> {
            var httpUrl = request.url();
            return request.method().equals("GET")
                    && "java".equals(httpUrl.queryParameter("q"))
                    && "ru".equals(httpUrl.queryParameter("lang"))
                    && httpUrl.queryParameter("blank") == null
                    && httpUrl.queryParameter("validBlankValue") == null
                    && httpUrl.queryParameter("skipNullValue") == null;
        }));
    }

    @Test
    void givenHttpErrorResponse_whenExecute_thenThrowsExecutorException() throws Exception {
        OkHttpClient client = mock(OkHttpClient.class);
        Call call = mock(Call.class);

        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(errorResponse());

        OkHttpHttpExecutor executor = new OkHttpHttpExecutor(client);

        ExecutorException exception = assertThrows(
                ExecutorException.class,
                () -> executor.execute(new URI("https://example.com").toURL(), Map.of())
        );

        assertEquals("http error: 500 body: server exploded", exception.getMessage());
    }

    @Test
    void givenIOException_whenExecute_thenThrowsExecutorException() throws Exception {
        OkHttpClient client = mock(OkHttpClient.class);
        Call call = mock(Call.class);
        URL url = new URI("https://example.com").toURL();

        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenThrow(new IOException("timeout"));

        OkHttpHttpExecutor executor = new OkHttpHttpExecutor(client);

        ExecutorException exception = assertThrows(
                ExecutorException.class,
                () -> executor.execute(url, Map.of())
        );

        assertEquals("request failed (network/timeout): " + url, exception.getMessage());
    }

    @Test
    void givenInvalidUrl_whenExecute_thenThrowsExecutorException() throws Exception {
        OkHttpClient client = mock(OkHttpClient.class);
        OkHttpHttpExecutor executor = new OkHttpHttpExecutor(client);

        URL url = new URI("mailto:test@example.com").toURL();

        assertThrows(ExecutorException.class, () -> executor.execute(url, Map.of()));
        verify(client, never()).newCall(any());
    }

    private static Response successResponse(String body) {
        Request request = new Request.Builder()
                .url("https://example.com")
                .get()
                .build();

        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(body, MediaType.get("application/json")))
                .build();
    }

    private static Response errorResponse() {
        Request request = new Request.Builder()
                .url("https://example.com")
                .get()
                .build();

        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(500)
                .message("ERROR")
                .body(ResponseBody.create("server exploded", MediaType.get("text/plain")))
                .build();
    }
}
