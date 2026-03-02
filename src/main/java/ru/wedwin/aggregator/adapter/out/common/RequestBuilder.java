package ru.wedwin.aggregator.adapter.out.common;

import okhttp3.HttpUrl;
import ru.wedwin.aggregator.domain.model.api.ApiParams;

import java.net.URL;
import java.util.Map;

public class RequestBuilder {
    public static URL buildGet(URL url, ApiParams params) {
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

        for (Map.Entry<String, String> entry: params.asMap().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || key.isBlank() || value == null) {
                continue;
            }
            builder.addQueryParameter(key, value);
        }

        return builder.build().url();
    }
}
