package ru.wedwin.aggregator.adapter.out.apis.weatherapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record WeatherApiResponse(
        Location location,
        Current current
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location(
            String name,
            String region,
            String country,
            double lat,
            double lon,
            String tzId,
            String localtimeEpoch,
            String localtime
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Current(
            String lastUpdatedEpoch,
            String lastUpdated,
            double tempC,
            double tempF,
            int isDay,
            Condition condition,
            double windMph,
            double windKph,
            int windDegree,
            String wingDir,
            int pressureMb,
            double pressureIn,
            double precipMm,
            int precipIn,
            int humidity,
            int cloud,
            double feelslikeC,
            double feelslikeF,
            double windchillC,
            double windchillF,
            double heatindexC,
            double heatindexF,
            double dewpointC,
            double dewpointF,
            int visKm,
            int visMiles,
            double uv,
            double gustMph,
            double gustKph

    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Condition(
                String text,
                String icon,
                int code
        ) {}
    }
}
