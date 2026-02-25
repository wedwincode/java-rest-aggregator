package ru.wedwin.aggregator.adapter.out.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.wedwin.aggregator.domain.exceptions.executor.ExecutorException;
import ru.wedwin.aggregator.domain.exceptions.executor.UnsuccessfulResponseException;
import ru.wedwin.aggregator.port.out.HttpExecutor;

import java.io.IOException;

public class OkHttpExecutor implements HttpExecutor {
    private final OkHttpClient client;

    public OkHttpExecutor() {
        client = new OkHttpClient();
    }

    @Override
    public String execute(String url) {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            }
            throw new UnsuccessfulResponseException();
        } catch (IOException e) {
            throw new ExecutorException("execution failed");
        }
    }
}
