package com.jdl.ghdata.githubapi.client;

import com.jdl.ghdata.githubapi.dto.GitHubApiUserRepoResponseDto;
import com.jdl.ghdata.githubapi.dto.GitHubApiUserResponseDto;
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

    public GitHubApiUserResponseDto getUser(String username) {
        return githubRestClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .body(GitHubApiUserResponseDto.class);
    }

    public List<GitHubApiUserRepoResponseDto> getUserRepos(String username) {
        return githubRestClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GitHubApiUserRepoResponseDto>>() {
                });
    }
}
