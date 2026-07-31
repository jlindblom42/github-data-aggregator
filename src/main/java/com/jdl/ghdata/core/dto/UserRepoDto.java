package com.jdl.ghdata.core.dto;

import com.jdl.ghdata.githubapi.dto.GitHubApiUserRepoResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserRepoDto(
        @Schema(example = "boysenberry-repo-1")
        String name,
        @Schema(example = "https://api.github.com/repos/octocat/boysenberry-repo-1")
        String url) {

    public static UserRepoDto fromGitHubUserResponse(GitHubApiUserRepoResponseDto gitHubUserRepo) {
        return new UserRepoDto(gitHubUserRepo.name(), gitHubUserRepo.url());
    }
}
