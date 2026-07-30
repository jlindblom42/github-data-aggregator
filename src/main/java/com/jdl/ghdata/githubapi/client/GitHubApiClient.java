package com.jdl.ghdata.githubapi.client;

import com.jdl.ghdata.githubapi.config.GitHubApiCacheConfig;
import com.jdl.ghdata.githubapi.dto.GitHubApiUserRepoResponseDto;
import com.jdl.ghdata.githubapi.dto.GitHubApiUserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class GitHubApiClient {

    private final RestClient githubRestClient;

    public GitHubApiClient(RestClient githubRestClient) {
        this.githubRestClient = githubRestClient;
    }

    @Cacheable(GitHubApiCacheConfig.USER_CACHE)
    public GitHubApiUserResponseDto getUser(String username) {
        log.info("Calling GitHub API for user [username={}]", username);
        try {
            return githubRestClient.get()
                    .uri("/users/{username}", username)
                    .retrieve()
                    .body(GitHubApiUserResponseDto.class);

        } catch (RestClientException e) {
            log.error("GitHub API call failed for user [username={}]: {}", username, e.getMessage());
            throw e;
        }
    }

    @Cacheable(GitHubApiCacheConfig.USER_REPOS_CACHE)
    public List<GitHubApiUserRepoResponseDto> getUserRepos(String username) {
        log.info("Calling GitHub API for user repos [username={}]", username);
        try {
            List<GitHubApiUserRepoResponseDto> repos = githubRestClient.get()
                    .uri("/users/{username}/repos", username)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<GitHubApiUserRepoResponseDto>>() {
                    });

            if (repos == null) {
                // For null safety
                repos = Collections.emptyList();
            }

            log.debug("GitHub API returned repos [username={}, repoCount={}]",
                    username, repos.size());

            return repos;

        } catch (RestClientException e) {
            log.error("GitHub API call failed for user repos [username={}]: {}", username, e.getMessage());
            throw e;
        }
    }
}
