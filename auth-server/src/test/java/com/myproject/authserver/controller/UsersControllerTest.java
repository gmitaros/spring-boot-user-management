package com.myproject.authserver.controller;

import com.myproject.authenticationcore.config.TokenGenerator;
import com.myproject.authenticationcore.model.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Sql(value = "/test_data/insert_mock_user.sql")
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenGenerator tokenGenerator;

    @MockBean
    private Authentication authentication;

    private String token;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(1L, "gmitaros@gmail.com", "password", Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
        token = tokenGenerator.createAccessToken(authenticatedUser);
    }

    @Test
    void userInfo() throws Exception {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(1L, "john.doe@example.com", "password", Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
        var token = tokenGenerator.createAccessToken(authenticatedUser);
        when(authentication.getPrincipal()).thenReturn(authenticatedUser);

        mockMvc.perform(get("/auth/user/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Test"))
                .andExpect(jsonPath("$.lastname").value("Integration"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void getAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/users")
                        .param("active", "true")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].firstname").value("Giorgos"))
                .andExpect(jsonPath("$.content[0].lastname").value("Mitaros"))
                .andExpect(jsonPath("$.content[0].email").value("gmitaros@gmail.com"))
                .andExpect(jsonPath("$.content[1].firstname").value("Test"))
                .andExpect(jsonPath("$.content[1].lastname").value("Integration"))
                .andExpect(jsonPath("$.content[1].email").value("john.doe@example.com"));
    }

    @Test
    void updateUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/auth/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"Jane\",\"lastname\":\"Doe\",\"email\":\"jane.doe@example.com\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.firstname").value("Jane"))
                .andExpect(jsonPath("$.lastname").value("Doe"))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/auth/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isAccepted());
    }

    @Test
    void deleteUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/auth/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[2]")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isAccepted());
    }


}