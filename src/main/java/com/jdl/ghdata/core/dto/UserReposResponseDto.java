package com.jdl.ghdata.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record UserReposResponseDto(
        @Schema(example = "octocat")
        String userName,
        @Schema(example = "The Octocat")
        String displayName,
        @Schema(example = "https://avatars.githubusercontent.com/u/583231?v=4")
        String avatar,
        @Schema(example = "San Francisco")
        String geoLocation,
        @Schema(example = "null", nullable = true)
        String email,
        @Schema(example = "https://api.github.com/users/octocat")
        String url,
        @Schema(example = "Tue, 25 Jan 2011 18:44:36 GMT")
        String createdAt,
        List<UserRepoDto> repos) {
}
