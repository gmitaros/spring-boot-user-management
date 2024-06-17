package com.myproject.emailservice.controller;

import com.myproject.authenticationcore.config.TokenGenerator;
import com.myproject.authenticationcore.model.AuthenticatedUser;
import com.myproject.emailservice.model.Email;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Sql(value = "/test_data/insert_emails.sql")
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenGenerator tokenGenerator;

    @MockBean
    private JavaMailSender javaMailSender;

    private String token;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(1L, "gmitaros@gmail.com", "password", Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
        token = tokenGenerator.createAccessToken(authenticatedUser);
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    void sendEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/emails/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("{\"recipientEmail\":\"recipient@example.com\",\"subject\":\"Test Subject\",\"message\":\"Test Body\",\"emailTemplate\":\"ACTIVATE_ACCOUNT\",\"userId\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    void getEmailsByUserId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/emails/user/12")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(12L))
                .andExpect(jsonPath("$[0].recipientEmail").value("gmitaros@yahoo.com"))
                .andExpect(jsonPath("$[0].subject").value("Welcome to My Project"))
                .andExpect(jsonPath("$[0].message").value("<html><body><p>Hello gmitaros@yahoo.com,</p><p>Thank you for registering on My Project.</body></html>"));
    }
}
