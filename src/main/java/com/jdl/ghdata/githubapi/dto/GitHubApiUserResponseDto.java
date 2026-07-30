package com.jdl.ghdata.githubapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubApiUserResponseDto(
        String login,
        String name,
        @JsonProperty("avatar_url")
        String avatarUrl,
        String location,
        String email,
        String url,
        @JsonProperty("created_at")
        OffsetDateTime createdAt) {
}
