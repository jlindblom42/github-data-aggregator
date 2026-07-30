package com.jdl.ghdata.controller;

import com.jdl.ghdata.dto.UserReposResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{username}")
    public UserReposResponseDto getUserAndUserRepos(@PathVariable String username) {
        return new UserReposResponseDto(
                username,
                "The Octocat",
                "https://avatars.githubusercontent.com/u/583231?v=4",
                "San Francisco",
                null,
                "https://api.github.com/users/%s".formatted(username),
                "Tue, 25 Jan 2011 18:44:36 GMT",
                List.of(new UserReposResponseDto.Repo(
                        "boysenberry-repo-1",
                        "https://api.github.com/repos/%s/boysenberry-repo-1".formatted(username)
                ))
        );
    }
}
