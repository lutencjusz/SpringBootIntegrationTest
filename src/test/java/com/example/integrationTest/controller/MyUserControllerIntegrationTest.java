package com.example.integrationTest.controller;

import com.example.integrationTest.models.MyUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
//@Transactional
public class MyUserControllerIntegrationTest {

    private static String jwtUser, jwtAdmin;
    private final MyUser user = new MyUser(1L, "user", "user", "USER");
    private final MyUser admin = new MyUser(2L, "admin", "admin", "ADMIN");

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16:0");

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    private void shouldUserBeAnUnauthorized(String user) throws Exception {
        this.mockMvc.perform(get("/" + user))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"))
                .andExpect(content().string(""));
    }

    void shouldRegisterUser(MyUser myUser) throws Exception {
        mockMvc.perform(post("/register/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(myUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(myUser.getId()))
                .andExpect(jsonPath("$.username").value(myUser.getUsername()))
                .andExpect(jsonPath("$.role").value(myUser.getRole()));
    }

    @Test
    @Order(1)
    void shouldGetMessageFromEveryone() throws Exception {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Witam nieautoryzowanego użytkownika"));
    }

    @Test
    @Order(2)
    void shouldUserBeAnUnAuthorized() throws Exception {
        shouldUserBeAnUnauthorized("user");
    }

    @Test
    @Order(3)
    void shouldAdminBeAnUnauthorized() throws Exception {
        shouldUserBeAnUnauthorized("admin");
    }

    @Test
    @Order(4)
    void shouldRegisterUser() throws Exception {
        shouldRegisterUser(user);
    }

    @Test
    @Order(5)
    void shouldRegisterAdmin() throws Exception {
        shouldRegisterUser(admin);
    }

    @Test
    @Order(6)
    void shouldAuthenticateUserAndGetToken() throws Exception {
        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").isString())
                .andDo(result -> this.jwtUser = result.getResponse().getContentAsString());
    }

    @Test
    @Order(7)
    void shouldAuthenticateAdminAndGetToken() throws Exception {
        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(admin)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").isString())
                .andDo(result -> this.jwtAdmin = result.getResponse().getContentAsString());
    }

    @Test
    @Order(8)
    void shouldUserBeAnAuthorized() throws Exception {
        mockMvc.perform(get("/user")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Witamy zautoryzowanego użytkownika"));
    }

    @Test
    @Order(9)
    void shouldAdminBeAnAuthorized() throws Exception {
        mockMvc.perform(get("/admin")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Witamy zautoryzowanego admina"));
    }

    @Test
    @Order(10)
    void shouldAdminBeAnUnauthorizedByUserToken() throws Exception {
        mockMvc.perform(get("/admin")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }




}
