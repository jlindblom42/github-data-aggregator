package com.jdl.ghdata.core.exception;

import com.jdl.ghdata.core.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.time.Instant;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String HEADER_GH_X_RATELIMIT_RESET = "x-ratelimit-reset";

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(HttpClientErrorException.NotFound ex) {
        log.error("GitHub API resource not found.", ex);
        // TODO: There's ambiguity here, 404 could be because the endpoint does not exist OR the
        //  entity/resource did not exist.  At first glance the error response from GitHub is too generic
        //  to distinguish, so leaving generic for now. Need to look into common practice for establishing
        //  a GitHub user exists.
        return errorResponse(HttpStatus.NOT_FOUND, "The requested GitHub resource was not found.");
    }

    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    public ResponseEntity<ErrorResponseDto> handleGitHubTooManyRequests(HttpClientErrorException.TooManyRequests ex) {
        log.error("GitHub API rate limit exceeded.", ex);

        ResponseEntity<ErrorResponseDto> response = errorResponse(HttpStatus.TOO_MANY_REQUESTS,
                "GitHub API rate limit exceeded. Retry after the indicated time.");

        String retryAfter = resolveRetryAfterSeconds(ex.getResponseHeaders());
        if (retryAfter != null) {
            return ResponseEntity.status(response.getStatusCode())
                    .header(HttpHeaders.RETRY_AFTER, retryAfter)
                    .body(response.getBody());
        }

        return response;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleGitHubClientError(HttpClientErrorException ex) {
        log.error("GitHub API rejected the request [status={}]", ex.getStatusCode(), ex);
        return errorResponse(ex.getStatusCode(), "GitHub API rejected the request.");
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleGitHubServerError(HttpServerErrorException ex) {
        log.error("GitHub API returned a server error [status={}]", ex.getStatusCode(), ex);
        return errorResponse(ex.getStatusCode(), "GitHub API is currently unavailable.");
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponseDto> handleRestClientError(RestClientException ex) {
        log.error("Failed to communicate with GitHub API", ex);
        return errorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Unable to reach GitHub API.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpectedError(Exception ex) {
        log.error("Unhandled exception", ex);
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }

    private static String resolveRetryAfterSeconds(HttpHeaders githubResponseHeaders) {
        if (githubResponseHeaders == null) {
            return null;
        }

        String retryAfter = githubResponseHeaders.getFirst(HttpHeaders.RETRY_AFTER);
        if (retryAfter != null) {
            return retryAfter;
        }

        String resetEpochSeconds = githubResponseHeaders.getFirst(HEADER_GH_X_RATELIMIT_RESET);
        if (resetEpochSeconds != null) {
            try {
                long secondsUntilReset = Long.parseLong(resetEpochSeconds) - Instant.now().getEpochSecond();
                return String.valueOf(Math.max(secondsUntilReset, 0));
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    private static ResponseEntity<ErrorResponseDto> errorResponse(HttpStatusCode status, String message) {
        HttpStatus resolved = HttpStatus.resolve(status.value());
        String reasonPhrase = resolved != null ? resolved.getReasonPhrase() : status.toString();
        return ResponseEntity.status(status)
                .body(new ErrorResponseDto(status.value(), reasonPhrase, message));
    }
}
