package com.jdl.ghdata.core.controller;

import com.jdl.ghdata.core.dto.UserReposResponseDto;
import com.jdl.ghdata.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public UserReposResponseDto getUserAndUserRepos(@PathVariable String username) {
        log.info("Received request for user and user repos [username={}]", username);
        UserReposResponseDto response = userService.getUserAndUserRepos(username);
        log.debug("Returning response for user and user repos [username={}, repoCount={}]",
                username, response.repos().size());
        return response;
    }
}
