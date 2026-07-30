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
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class UserService {

    private final GitHubApiClient githubApiClient;

    public UserService(GitHubApiClient githubApiClient) {
        this.githubApiClient = githubApiClient;
    }

    public UserReposResponseDto getUserAndUserRepos(String username) {
        log.debug("Fetching GitHub user and user repos in parallel [username={}]", username);

        // TODO: Need to be mindful of rate limiting, we should track frequency over time.
        //  (The api call caching should help mitigate, but won't help if we get a batch of varied usernames)
        CompletableFuture<GitHubApiUserResponseDto> userFuture =
                CompletableFuture.supplyAsync(() -> githubApiClient.getUser(username));
        CompletableFuture<List<GitHubApiUserRepoResponseDto>> reposFuture =
                CompletableFuture.supplyAsync(() -> githubApiClient.getUserRepos(username));

        GitHubApiUserResponseDto user = userFuture.join();
        List<GitHubApiUserRepoResponseDto> repos = reposFuture.join();

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
