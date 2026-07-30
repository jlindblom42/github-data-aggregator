package com.jdl.ghdata.controller;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUserAndUserRepos_returnsUserAndReposJson() throws Exception {
        String actualJson = mockMvc.perform(MockMvcRequestBuilders.get("/users/octocat"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedJson = StreamUtils.copyToString(
                new ClassPathResource("com/jdl/ghdata/controller/user-repos-response.json").getInputStream(),
                StandardCharsets.UTF_8);

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.STRICT);
    }
}
