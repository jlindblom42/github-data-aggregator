package com.jdl.ghdata.githubapi.client;

import com.jdl.ghdata.githubapi.config.GitHubApiCacheConfig;
import com.jdl.ghdata.githubapi.dto.GitHubApiUserRepoResponseDto;
import com.jdl.ghdata.githubapi.dto.GitHubApiUserResponseDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GitHubApiClient {

    private final RestClient githubRestClient;

    public GitHubApiClient(RestClient githubRestClient) {
        this.githubRestClient = githubRestClient;
    }

    @Cacheable(GitHubApiCacheConfig.USER_CACHE)
    public GitHubApiUserResponseDto getUser(String username) {
        return githubRestClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .body(GitHubApiUserResponseDto.class);
    }

    @Cacheable(GitHubApiCacheConfig.USER_REPOS_CACHE)
    public List<GitHubApiUserRepoResponseDto> getUserRepos(String username) {
        return githubRestClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GitHubApiUserRepoResponseDto>>() {
                });
    }
}
