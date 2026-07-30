package com.jdl.ghdata.core.exception;

import com.jdl.ghdata.core.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(HttpClientErrorException.NotFound ex) {
        log.error("GitHub API resource not found.", ex);
        // TODO: There's ambiguity here, 404 could be because the endpoint does not exist OR the
        //  entity/resource did not exist.  At first glance the error response from GitHub is too generic
        //  to distinguish, so leaving generic for now. Need to look into common practice for establishing
        //  a GitHub user exists.
        return errorResponse(HttpStatus.NOT_FOUND, "The requested GitHub resource was not found.");
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleGitHubClientError(HttpClientErrorException ex) {
        log.error("GitHub API rejected the request [status={}]", ex.getStatusCode(), ex);
        return errorResponse(HttpStatus.BAD_GATEWAY, "GitHub API rejected the request.");
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleGitHubServerError(HttpServerErrorException ex) {
        log.error("GitHub API returned a server error [status={}]", ex.getStatusCode(), ex);
        return errorResponse(HttpStatus.BAD_GATEWAY, "GitHub API is currently unavailable.");
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

    private static ResponseEntity<ErrorResponseDto> errorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponseDto(status.value(), status.getReasonPhrase(), message));
    }
}
