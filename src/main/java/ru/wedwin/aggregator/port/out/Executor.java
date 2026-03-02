package ru.wedwin.aggregator.port.out;

import java.net.URL;
import java.util.Map;

public interface Executor {
    String execute(URL url, Map<String, String> params);
}
