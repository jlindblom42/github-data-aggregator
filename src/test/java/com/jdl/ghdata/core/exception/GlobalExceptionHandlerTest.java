package com.jdl.ghdata.core.exception;

import com.jdl.ghdata.core.controller.UserController;
import com.jdl.ghdata.core.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.Instant;

@WebMvcTest(UserController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void notFoundFromGitHubApi_returns404() throws Exception {
        BDDMockito.given(userService.getUserAndUserRepos("octocat")).willThrow(
                HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, null, null));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/octocat"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Not Found"));
    }

    @Test
    void tooManyRequestsFromGitHubApi_returns429() throws Exception {
        HttpHeaders githubHeaders = new HttpHeaders();
        githubHeaders.add(HttpHeaders.RETRY_AFTER, "30");
        BDDMockito.given(userService.getUserAndUserRepos("octocat")).willThrow(
                HttpClientErrorException.create(HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests", githubHeaders, null, null));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/octocat"))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(429))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.RETRY_AFTER, "30"));
    }

    @Test
    void tooManyRequestsFromGitHubApi_fallsBackToRateLimitResetHeader() throws Exception {
        long resetEpochSeconds = Instant.now().plusSeconds(60).getEpochSecond();
        HttpHeaders githubHeaders = new HttpHeaders();
        githubHeaders.add("x-ratelimit-reset", String.valueOf(resetEpochSeconds));
        BDDMockito.given(userService.getUserAndUserRepos("octocat")).willThrow(
                HttpClientErrorException.create(HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests", githubHeaders, null, null));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/octocat"))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(429))
                .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.RETRY_AFTER));
    }

    @Test
    void otherClientErrorFromGitHubApi_passesThroughStatus() throws Exception {
        BDDMockito.given(userService.getUserAndUserRepos("octocat")).willThrow(
                HttpClientErrorException.create(HttpStatus.FORBIDDEN, "Forbidden", HttpHeaders.EMPTY, null, null));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/octocat"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(403))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void serverErrorFromGitHubApi_passesThroughStatus() throws Exception {
        BDDMockito.given(userService.getUserAndUserRepos("octocat")).willThrow(
                HttpServerErrorException.create(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", HttpHeaders.EMPTY, null, null));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/octocat"))
                .andExpect(MockMvcResultMatchers.status().isServiceUnavailable())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(503))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Service Unavailable"));
    }

    @Test
    void restClientCommunicationFailure_returns503() throws Exception {
        BDDMockito.given(userService.getUserAndUserRepos("octocat")).willThrow(
                new ResourceAccessException("Connection refused"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/octocat"))
                .andExpect(MockMvcResultMatchers.status().isServiceUnavailable())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(503));
    }

    @Test
    void unexpectedException_returns500() throws Exception {
        BDDMockito.given(userService.getUserAndUserRepos("octocat")).willThrow(
                new RuntimeException("boom"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/octocat"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(500));
    }
}
