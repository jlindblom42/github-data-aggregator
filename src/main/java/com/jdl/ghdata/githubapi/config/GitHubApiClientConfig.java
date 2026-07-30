package com.jdl.ghdata.githubapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

@Slf4j
@Configuration
public class GitHubApiClientConfig {

    private static final String HEADER_GH_X_RATELIMIT_LIMIT = "x-ratelimit-limit";
    private static final String HEADER_GH_X_RATELIMIT_REMAINING = "x-ratelimit-remaining";
    private static final String HEADER_GH_X_RATELIMIT_USED = "x-ratelimit-used";
    private static final String HEADER_GH_X_RATELIMIT_RESET = "x-ratelimit-reset";
    private static final String HEADER_GH_X_RATELIMIT_RESOURCE = "x-ratelimit-resource";

    @Bean
    public RestClient gitHubRestClient(@Value("${github.api.base-url}") String githubApiBaseUrl, JsonMapper jsonMapper) {
        log.info("Configuring GitHub API RestClient [baseUrl={}]", githubApiBaseUrl);
        return RestClient.builder()
                .baseUrl(githubApiBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader(HttpHeaders.USER_AGENT, "github-data-aggregator")
                .requestInterceptor(this::logRateLimitHeaders)
                .configureMessageConverters(converters -> converters
                        .withJsonConverter(new JacksonJsonHttpMessageConverter(jsonMapper))
                        .registerDefaults())
                .build();
    }

    private ClientHttpResponse logRateLimitHeaders(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        HttpHeaders headers = response.getHeaders();
        // TODO: Consider de-escalating happy case to "debug" and escalating to "warn" if values reach a
        //  configured threshold.
        log.info("GitHub API rate limit [limit={}, remaining={}, used={}, reset={}, resource={}]",
                headers.getFirst(HEADER_GH_X_RATELIMIT_LIMIT),
                headers.getFirst(HEADER_GH_X_RATELIMIT_REMAINING),
                headers.getFirst(HEADER_GH_X_RATELIMIT_USED),
                headers.getFirst(HEADER_GH_X_RATELIMIT_RESET),
                headers.getFirst(HEADER_GH_X_RATELIMIT_RESOURCE));
        return response;
    }
}
