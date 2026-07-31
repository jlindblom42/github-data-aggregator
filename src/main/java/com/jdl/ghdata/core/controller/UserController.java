package com.jdl.ghdata.core.controller;

import com.jdl.ghdata.core.dto.ErrorResponseDto;
import com.jdl.ghdata.core.dto.UserReposResponseDto;
import com.jdl.ghdata.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Look up a GitHub user's profile combined with their public repositories.")
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    @Operation(summary = "Get user and user repos",
            description = "Retrieves a GitHub user's profile combined with their public repositories.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "User and repos found."),
            @ApiResponse(responseCode = "404",
                    description = "The GitHub user does not exist (or the resource was not found).",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500",
                    description = "An unexpected error occurred.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "502",
                    description = "GitHub rejected the request or returned a server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "503",
                    description = "The GitHub API could not be reached.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    public UserReposResponseDto getUserAndUserRepos(
            @Parameter(description = "The GitHub username to look up.", example = "octocat")
            @PathVariable String username) {
        log.info("Received request for user and user repos [username={}]", username);
        UserReposResponseDto response = userService.getUserAndUserRepos(username);
        log.debug("Returning response for user and user repos [username={}, repoCount={}]",
                username, response.repos().size());
        return response;
    }
}
