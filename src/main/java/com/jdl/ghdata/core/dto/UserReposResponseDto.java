package com.jdl.ghdata.core.dto;

import java.util.List;

public record UserReposResponseDto(
        String userName,
        String displayName,
        String avatar,
        String geoLocation,
        String email,
        String url,
        String createdAt,
        List<UserRepoDto> repos) {
}
