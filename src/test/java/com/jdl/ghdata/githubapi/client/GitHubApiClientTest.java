package com.jdl.ghdata.githubapi.client;

import com.jdl.ghdata.githubapi.dto.GitHubApiUserRepoResponseDto;
import com.jdl.ghdata.githubapi.dto.GitHubApiUserResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

class GitHubApiClientTest {

    private MockRestServiceServer mockServer;
    private GitHubApiClient githubApiClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder restClientBuilder = RestClient.builder().baseUrl("https://api.github.com");
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        githubApiClient = new GitHubApiClient(restClientBuilder.build());
    }

    @Test
    void getUser_mapsGitHubUserResponse() throws IOException {
        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.github.com/users/octocat"))
                .andRespond(MockRestResponseCreators.withSuccess(
                        readJson("get-user-response.json"), MediaType.APPLICATION_JSON));

        GitHubApiUserResponseDto user = githubApiClient.getUser("octocat");

        Assertions.assertThat(user).isEqualTo(new GitHubApiUserResponseDto(
                "octocat",
                "The Octocat",
                "https://avatars.githubusercontent.com/u/583231?v=4",
                "San Francisco",
                null,
                "https://api.github.com/users/octocat",
                OffsetDateTime.parse("2011-01-25T18:44:36Z")
        ));
    }

    @Test
    void getUserRepos_mapsGitHubReposResponse() throws IOException {
        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.github.com/users/octocat/repos"))
                .andRespond(MockRestResponseCreators.withSuccess(
                        readJson("get-user-repos-response.json"), MediaType.APPLICATION_JSON));

        List<GitHubApiUserRepoResponseDto> repos = githubApiClient.getUserRepos("octocat");

        Assertions.assertThat(repos).containsExactly(new GitHubApiUserRepoResponseDto(
                "boysenberry-repo-1",
                "https://api.github.com/repos/octocat/boysenberry-repo-1"
        ));
    }

    private static String readJson(String fileName) throws IOException {
        return StreamUtils.copyToString(
                new ClassPathResource("com/jdl/ghdata/githubapi.client/%s".formatted(fileName)).getInputStream(),
                StandardCharsets.UTF_8);
    }
}
