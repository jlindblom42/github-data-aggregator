package com.jdl.ghdata.core.dto;

public record ErrorResponseDto(
        int status,
        String error,
        String message) {
}
