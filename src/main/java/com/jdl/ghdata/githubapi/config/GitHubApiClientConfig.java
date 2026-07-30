package com.jdl.ghdata.githubapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Configuration
public class GitHubApiClientConfig {

    @Bean
    public RestClient gitHubRestClient(@Value("${github.api.base-url}") String githubApiBaseUrl, JsonMapper jsonMapper) {
        log.info("Configuring GitHub API RestClient [baseUrl={}]", githubApiBaseUrl);
        return RestClient.builder()
                .baseUrl(githubApiBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader(HttpHeaders.USER_AGENT, "github-data-aggregator")
                .configureMessageConverters(converters -> converters
                        .withJsonConverter(new JacksonJsonHttpMessageConverter(jsonMapper))
                        .registerDefaults())
                .build();
    }
}
