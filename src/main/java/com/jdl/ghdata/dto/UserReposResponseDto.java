package com.jdl.ghdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UserReposResponseDto(
        @JsonProperty("user_name")
        String userName,
        @JsonProperty("display_name")
        String displayName,
        String avatar,
        @JsonProperty("geo_location")
        String geoLocation,
        String email,
        String url,
        @JsonProperty("created_at")
        String createdAt,
        List<Repo> repos) {

    public record Repo(String name, String url) {
    }
}
