package com.jdl.ghdata.core.dto;

import com.jdl.ghdata.githubapi.dto.GitHubApiUserRepoResponseDto;

public record UserRepoDto(
        String name,
        String url) {

    public static UserRepoDto fromGitHubUserResponse(GitHubApiUserRepoResponseDto gitHubUserRepo) {
        return new UserRepoDto(gitHubUserRepo.name(), gitHubUserRepo.url());
    }
}
