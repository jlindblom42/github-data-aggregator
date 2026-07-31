package com.jdl.ghdata.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponseDto(
        @Schema(example = "404")
        int status,
        @Schema(example = "Not Found")
        String error,
        @Schema(example = "The requested GitHub resource was not found.")
        String message) {
}
