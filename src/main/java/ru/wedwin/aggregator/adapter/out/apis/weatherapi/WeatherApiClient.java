package ru.wedwin.aggregator.adapter.out.apis.weatherapi;

import ru.wedwin.aggregator.adapter.out.apis.AbstractApiClient;
import ru.wedwin.aggregator.adapter.out.apis.weatherapi.dto.WeatherApiResponse;
import ru.wedwin.aggregator.adapter.out.common.EnvReader;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ParamSpec;

import java.util.List;

public class WeatherApiClient extends AbstractApiClient<WeatherApiResponse> {
    @Override
    public ApiId id() {
        return new ApiId("weatherapi");
    }

    @Override
    public String url() {
        return "https://api.weatherapi.com/v1/current.json";
    }

    @Override
    public String displayName() {
        return "Weather API Client";
    }

    @Override
    public List<ParamSpec> supportedParams() {
        return List.of(
                new ParamSpec("key", true, EnvReader.get("WEATHER_API_KEY"), "api key (put it in the .env file)"),
                new ParamSpec("q", true, "Saint-Petersburg", "City name"),
                new ParamSpec("lang", false, "RU", "response language")
        );
    }

    @Override
    protected Class<WeatherApiResponse> dtoClass() {
        return WeatherApiResponse.class;
    }

}
