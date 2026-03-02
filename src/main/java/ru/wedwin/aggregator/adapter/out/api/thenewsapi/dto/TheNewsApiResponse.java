package ru.wedwin.aggregator.adapter.out.api.thenewsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

// Medium article about records as DTOs:
// https://medium.com/@anurag.ydv36/java-records-jackson-the-cleanest-dto-setup-ive-ever-built-235658b4df5b

// JsonProperty (@JsonProperty("published_at")) alternative:
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public record TheNewsApiResponse(
        Meta meta,
        List<Data> data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Meta(
           Integer found,
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
