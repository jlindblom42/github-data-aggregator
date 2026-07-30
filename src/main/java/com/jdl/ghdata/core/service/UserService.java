package com.jdl.ghdata.core.service;

import com.jdl.ghdata.core.dto.UserRepoDto;
import com.jdl.ghdata.core.dto.UserReposResponseDto;
import com.jdl.ghdata.githubapi.client.GitHubApiClient;
import com.jdl.ghdata.githubapi.dto.GitHubApiUserRepoResponseDto;
import com.jdl.ghdata.githubapi.dto.GitHubApiUserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final GitHubApiClient githubApiClient;

    public UserService(GitHubApiClient githubApiClient) {
        this.githubApiClient = githubApiClient;
    }

    public UserReposResponseDto getUserAndUserRepos(String username) {
        log.debug("Fetching GitHub user [username={}]", username);
        GitHubApiUserResponseDto user = githubApiClient.getUser(username);

        log.debug("Fetching GitHub user repos [username={}]", username);
        List<GitHubApiUserRepoResponseDto> repos = githubApiClient.getUserRepos(username);

        return new UserReposResponseDto(
                user.login(),
                user.name(),
                user.avatarUrl(),
                user.location(),
                user.email(),
                user.url(),
                DateTimeFormatter.RFC_1123_DATE_TIME.format(user.createdAt()),
                repos.stream()
                        .map(UserRepoDto::fromGitHubUserResponse)
                        .toList()
        );
    }
}
