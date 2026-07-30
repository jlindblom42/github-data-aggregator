package com.jdl.ghdata.githubapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class GitHubApiClientConfig {

    @Bean
    public RestClient gitHubRestClient(@Value("${github.api.base-url}") String githubApiBaseUrl) {
        return RestClient.builder()
                .baseUrl(githubApiBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader(HttpHeaders.USER_AGENT, "github-data-aggregator")
                .build();
    }
}
