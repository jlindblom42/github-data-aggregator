package com.jdl.ghdata.githubapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubApiUserRepoResponseDto(
        String name,
        String url) {
}
