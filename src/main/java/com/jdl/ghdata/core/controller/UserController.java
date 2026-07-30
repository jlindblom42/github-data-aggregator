package com.jdl.ghdata.core.controller;

import com.jdl.ghdata.core.dto.UserReposResponseDto;
import com.jdl.ghdata.core.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public UserReposResponseDto getUserAndUserRepos(@PathVariable String username) {
        return userService.getUserAndUserRepos(username);
    }
}
