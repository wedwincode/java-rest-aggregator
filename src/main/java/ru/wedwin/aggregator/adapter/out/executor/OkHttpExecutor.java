package ru.wedwin.aggregator.adapter.out.executor;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ru.wedwin.aggregator.port.out.Executor;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

public class OkHttpExecutor implements Executor {
    private final OkHttpClient client;

    public OkHttpExecutor() {
        client = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .writeTimeout(Duration.ofSeconds(10))
                .callTimeout(Duration.ofSeconds(15))
                .build();
    }

    @Override
    public String execute(URL url, Map<String, String> params) {
        Request request = buildGet(url, params);
        Call call = client.newCall(request);

        try (Response response = call.execute()) {
            int code = response.code();
            if (!response.isSuccessful()) {
                String errBody = safeBodyToString(response.body());
                throw new RuntimeException("http error: " + code + " body: " + trim(errBody, 500));
            }
            return safeBodyToString(response.body());
        } catch (IOException e) {
            throw new RuntimeException("request failed (network/timeout): " + url, e);
        }
    }

    private static Request buildGet(URL url, Map<String, String> params) {
        HttpUrl base;
        try {
            base = HttpUrl.get(url);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid url: " + url, e);
        }
        if (base == null) {
            throw new IllegalArgumentException("invalid url: " + url);
        }

        HttpUrl.Builder builder = base.newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key == null || key.isBlank() || value == null) {
                    continue;
                }
                builder.addQueryParameter(key, value);
            }
        }

        HttpUrl finalUrl = builder.build();

        return new Request.Builder()
                .url(finalUrl)
                .get()
                .build();
    }

    private static String safeBodyToString(ResponseBody body) throws IOException {
        if (body == null) {
            return "";
        }
        return body.string();
    }

    private static String trim(String s, int max) {
        if (s == null) {
            return "";
        }
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max);
    }

}
