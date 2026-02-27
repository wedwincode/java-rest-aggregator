package ru.wedwin.aggregator.adapter.out.apis.newsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NewsApiResponse(
        String status,
        Integer totalResults,
        List<Article> articles
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Article(
            Source source,
            String author,
            String title,
            String description,
            String url,
            String urlToImage,
            String publishedAt,
            String content
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Source(
            String id,
            String name
    ) {}
}