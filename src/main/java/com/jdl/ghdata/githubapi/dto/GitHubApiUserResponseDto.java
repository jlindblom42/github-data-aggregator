package com.jdl.ghdata.githubapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record GitHubApiUserResponseDto(
        String login,
        String name,
        String avatarUrl,
        String location,
        String email,
        String url,
        OffsetDateTime createdAt) {
}
