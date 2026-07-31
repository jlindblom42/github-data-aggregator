package com.jdl.ghdata.core.service;

import com.jdl.ghdata.core.dto.UserRepoDto;
import com.jdl.ghdata.core.dto.UserReposResponseDto;
import com.jdl.ghdata.githubapi.client.GitHubApiClient;
import com.jdl.ghdata.githubapi.dto.GitHubApiUserRepoResponseDto;
import com.jdl.ghdata.githubapi.dto.GitHubApiUserResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.time.OffsetDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private GitHubApiClient githubApiClient;

    @Test
    void getUserAndUserRepos_mergesGitHubUserAndReposIntoResponseDto() {
        UserService userService = new UserService(githubApiClient);

        BDDMockito.given(githubApiClient.getUser("octocat")).willReturn(new GitHubApiUserResponseDto(
                "octocat",
                "The Octocat",
                "https://avatars.githubusercontent.com/u/583231?v=4",
                "San Francisco",
                null,
                "https://api.github.com/users/octocat",
                OffsetDateTime.parse("2011-01-25T18:44:36Z")
        ));
        BDDMockito.given(githubApiClient.getUserRepos("octocat")).willReturn(List.of(
                new GitHubApiUserRepoResponseDto("boysenberry-repo-1", "https://api.github.com/repos/octocat/boysenberry-repo-1")
        ));

        UserReposResponseDto result = userService.getUserAndUserRepos("octocat");

        Assertions.assertThat(result).isEqualTo(new UserReposResponseDto(
                "octocat",
                "The Octocat",
                "https://avatars.githubusercontent.com/u/583231?v=4",
                "San Francisco",
                null,
                "https://api.github.com/users/octocat",
                "Tue, 25 Jan 2011 18:44:36 GMT",
                List.of(new UserRepoDto(
                        "boysenberry-repo-1",
                        "https://api.github.com/repos/octocat/boysenberry-repo-1"
                ))
        ));
    }

    @Test
    void getUserAndUserRepos_unwrapsCompletionExceptionFromAsyncGitHubCall() {
        UserService userService = new UserService(githubApiClient);

        BDDMockito.given(githubApiClient.getUser("octocat")).willThrow(
                HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, null, null));
        BDDMockito.given(githubApiClient.getUserRepos("octocat")).willReturn(List.of());

        Assertions.assertThatThrownBy(() -> userService.getUserAndUserRepos("octocat"))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }
}
