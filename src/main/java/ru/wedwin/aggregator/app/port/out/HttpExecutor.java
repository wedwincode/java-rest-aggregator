package ru.wedwin.aggregator.app.port.out;

import java.net.URL;
import java.util.Map;

public interface HttpExecutor {
    String execute(URL url, Map<String, String> params);
}
