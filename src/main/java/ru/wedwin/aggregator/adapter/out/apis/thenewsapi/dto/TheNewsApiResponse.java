package ru.wedwin.aggregator.adapter.out.apis.thenewsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

// JsonProperty (@JsonProperty("published_at")) alternative:
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public record TheNewsApiResponse(
        Meta meta,
        List<Data> data
) {
    // todo check medium page about records dto
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Meta(
           Integer found, // todo what to do if we want to rename field (check annotations for jackson)
           Integer returned,
           Integer limit,
           Integer page
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Data(
           String uuid,
           String title,
           String description,
           String keywords,
           String snippet,
           String url,
           String imageUrl,
           String language,
           String publishedAt,
           String source,
           List<String> categories,
           String relevanceScore,
           String locale
    ) {}
}
