package ru.wedwin.aggregator.adapter.out.common;

import ru.wedwin.aggregator.domain.model.api.ApiParams;

import java.util.Map;

public class RequestBuilder {
    public static String build(String url, ApiParams params) {
        Map<String, String> paramsMap = params.asMap();
        if (paramsMap.isEmpty()) {
            return url;
        }

        StringBuilder sb = new StringBuilder(url);
        boolean isFirstParam = true;
        for (Map.Entry<String, String> entry: paramsMap.entrySet()) {
            if (isFirstParam) {
                sb.append("?");
                isFirstParam = false;
            } else {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        return sb.toString();
    }
}
