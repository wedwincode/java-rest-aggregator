package ru.wedwin.aggregator.adapter.out.api.weatherapi;

import ru.wedwin.aggregator.adapter.out.api.AbstractApiClient;
import ru.wedwin.aggregator.adapter.out.api.weatherapi.dto.WeatherApiResponse;
import ru.wedwin.aggregator.adapter.out.common.EnvReader;
import ru.wedwin.aggregator.domain.model.api.ApiDefinition;
import ru.wedwin.aggregator.domain.model.api.ParamMeta;

public class WeatherApiClient extends AbstractApiClient<WeatherApiResponse> {
    @Override
    public ApiDefinition definition() {
        return new ApiDefinition(
                "weatherapi",
                "https://api.weatherapi.com/v1/current.json",
                "Weather API Client",
                new ParamMeta("key", true, EnvReader.get("WEATHER_API_KEY"), "api key (put it in the .env file)"),
                new ParamMeta("q", true, "Saint-Petersburg", "City name"),
                new ParamMeta("lang", false, "RU", "response language")
        );
    }

    @Override
    protected Class<WeatherApiResponse> dtoClass() {
        return WeatherApiResponse.class;
    }

}
