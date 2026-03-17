package ru.wedwin.aggregator.adapter.out.api.testapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TestApiResponse(
        Integer id,
        String name
) {
}
