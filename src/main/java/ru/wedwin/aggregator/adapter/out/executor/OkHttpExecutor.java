package ru.wedwin.aggregator.adapter.out.executor;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.wedwin.aggregator.domain.exception.executor.UnsuccessfulResponseException;
import ru.wedwin.aggregator.port.out.Executor;

import java.io.IOException;
import java.net.URL;

public class OkHttpExecutor implements Executor {
    private final OkHttpClient client;

    public OkHttpExecutor() {
        client = new OkHttpClient();
    }

    @Override
    public String execute(URL url) {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            }
            throw new UnsuccessfulResponseException();
        } catch (IOException e) {
            throw new RuntimeException("execution failed", e);
        }
    }

}
