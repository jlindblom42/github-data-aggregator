package com.jdl.ghdata.core.controller;

import com.jdl.ghdata.config.JacksonConfig;
import com.jdl.ghdata.core.dto.UserRepoDto;
import com.jdl.ghdata.core.dto.UserReposResponseDto;
import com.jdl.ghdata.core.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@WebMvcTest(UserController.class)
@Import(JacksonConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void getUserAndUserRepos_returnsUserAndReposJson() throws Exception {
        BDDMockito.given(userService.getUserAndUserRepos("octocat")).willReturn(new UserReposResponseDto(
                "octocat",
                "The Octocat",
                "https://avatars.githubusercontent.com/u/583231?v=4",
                "San Francisco",
                null,
                "https://api.github.com/users/octocat",
                "Tue, 25 Jan 2011 18:44:36 GMT",
                List.of(new UserRepoDto(
                        "boysenberry-repo-1",
                        "https://api.github.com/repos/octocat/boysenberry-repo-1"
                ))
        ));

        String actualJson = mockMvc.perform(MockMvcRequestBuilders.get("/users/octocat"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedJson = StreamUtils.copyToString(
                new ClassPathResource("com/jdl/ghdata/core/controller/user-repos-response.json").getInputStream(),
                StandardCharsets.UTF_8);

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.STRICT);
    }
}
