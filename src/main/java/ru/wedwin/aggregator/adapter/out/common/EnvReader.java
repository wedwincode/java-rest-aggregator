package ru.wedwin.aggregator.adapter.out.common;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvReader {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    public static String get(String envName) {
        return dotenv.get(envName, "");
    }
}
