package ru.wedwin.aggregator.adapter.out.apis.weatherapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherApiResponse(
        Location location,
        Current current
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Location(
            String name,
            String region,
            String country,
            Double lat,
            Double lon,
            String tzId,
            String localtimeEpoch,
            String localtime
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Current(
            String lastUpdatedEpoch,
            String lastUpdated,
            Double tempC,
            Double tempF,
            Integer isDay,
            Condition condition,
            Double windMph,
            Double windKph,
            Integer windDegree,
            String windDir,
            Integer pressureMb,
            Double pressureIn,
            Double precipMm,
            Integer precipIn,
            Integer humidity,
            Integer cloud,
            Double feelslikeC,
            Double feelslikeF,
            Double windchillC,
            Double windchillF,
            Double heatindexC,
            Double heatindexF,
            Double dewpointC,
            Double dewpointF,
            Integer visKm,
            Integer visMiles,
            Double uv,
            Double gustMph,
            Double gustKph

    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Condition(
                String text,
                String icon,
                Integer code
        ) {}
    }
}
