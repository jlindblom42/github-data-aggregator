package com.jdl.ghdata.githubapi.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
@EnableCaching
public class GitHubApiCacheConfig {

    public static final String USER_CACHE = "githubUser";
    public static final String USER_REPOS_CACHE = "githubUserRepos";

    @Bean
    public CacheManager cacheManager(
            @Value("${github.api.cache.ttl:PT1M}") Duration cacheTtl,
            @Value("${github.api.cache.max-size:1000}") long cacheMaxSize) {
        log.info("Configuring GitHub API caches [ttl={}, maxSize={}]", cacheTtl, cacheMaxSize);
        // Note: Caches kept separate for now to allow for independent usage
        // in case non-merge use case scenarios surface later.
        // TODO: Consider caching at service layer instead if no other use case scenarios exist,
        //  eliminating wasteful merge logic.
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(USER_CACHE, USER_REPOS_CACHE);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(cacheTtl)
                .maximumSize(cacheMaxSize));
        return cacheManager;
    }
}
