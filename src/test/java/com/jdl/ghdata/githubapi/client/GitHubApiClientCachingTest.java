package com.jdl.ghdata.githubapi.client;

import com.jdl.ghdata.githubapi.config.GitHubApiCacheConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@SpringBootTest(classes = {GitHubApiCacheConfig.class, GitHubApiClient.class})
@Import(GitHubApiClientCachingTest.MockRestClientConfig.class)
class GitHubApiClientCachingTest {

    @Autowired
    private GitHubApiClient githubApiClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        mockServer.reset();
        cacheManager.getCacheNames().forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    @Test
    void getUser_isServedFromCacheOnSubsequentCalls() throws IOException {
        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.github.com/users/octocat"))
                .andRespond(MockRestResponseCreators.withSuccess(
                        readJson("get-user-response.json"), MediaType.APPLICATION_JSON));

        githubApiClient.getUser("octocat");
        githubApiClient.getUser("octocat");

        mockServer.verify();
    }

    @Test
    void getUserRepos_isServedFromCacheOnSubsequentCalls() throws IOException {
        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.github.com/users/octocat/repos"))
                .andRespond(MockRestResponseCreators.withSuccess(
                        readJson("get-user-repos-response.json"), MediaType.APPLICATION_JSON));

        githubApiClient.getUserRepos("octocat");
        githubApiClient.getUserRepos("octocat");

        mockServer.verify();
    }

    @Test
    void getUser_fetchesAgainForDifferentUsername() throws IOException {
        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.github.com/users/octocat"))
                .andRespond(MockRestResponseCreators.withSuccess(
                        readJson("get-user-response.json"), MediaType.APPLICATION_JSON));
        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.github.com/users/defunkt"))
                .andRespond(MockRestResponseCreators.withSuccess(
                        readJson("get-user-response.json"), MediaType.APPLICATION_JSON));

        githubApiClient.getUser("octocat");
        githubApiClient.getUser("defunkt");

        mockServer.verify();
    }

    private static String readJson(String fileName) throws IOException {
        return StreamUtils.copyToString(
                new ClassPathResource("com/jdl/ghdata/githubapi.client/%s".formatted(fileName)).getInputStream(),
                StandardCharsets.UTF_8);
    }

    @TestConfiguration
    static class MockRestClientConfig {

        @Bean
        RestClient.Builder githubRestClientBuilder() {
            return RestClient.builder().baseUrl("https://api.github.com");
        }

        @Bean
        MockRestServiceServer mockServer(RestClient.Builder githubRestClientBuilder) {
            return MockRestServiceServer.bindTo(githubRestClientBuilder).build();
        }

        @Bean
        RestClient githubRestClient(RestClient.Builder githubRestClientBuilder, MockRestServiceServer mockServer) {
            return githubRestClientBuilder.build();
        }
    }
}
