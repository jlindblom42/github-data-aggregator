package com.jdl.ghdata.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gitHubDataAggregatorOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("GitHub Data Aggregator")
                        .description("Service that surfaces endpoints facilitating GitHub data aggregation.")
                        .version("v1"));
    }
}
