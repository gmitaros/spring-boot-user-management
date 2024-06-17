package com.myproject.authserver.controller;

import com.myproject.authenticationcore.config.TokenGenerator;
import com.myproject.authenticationcore.model.Token;
import com.myproject.authserver.dto.LoginRequest;
import com.myproject.authserver.dto.RegistrationRequest;
import com.myproject.authserver.dto.UserDto;
import com.myproject.authserver.model.Role;
import com.myproject.authserver.model.User;
import com.myproject.authserver.service.UserService;
import com.myproject.authserver.utils.DtoMapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    private UserService userService = mock(UserService.class);
    private TokenGenerator tokenGenerator = mock(TokenGenerator.class);

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(userService, tokenGenerator);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register() {
        RegistrationRequest request = new RegistrationRequest("John", "Doe", "john.doe@example.com", "password123");
        var user = getMockUser();
        when(userService.register(request)).thenReturn(user);
        ResponseEntity<?> response = controller.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(DtoMapperUtil.toUserDto(user), ((UserDto) response.getBody()));
        verify(userService, times(1)).register(request);
    }

    @Test
    void authenticate() throws IOException {
        LoginRequest request = LoginRequest.builder()
                .email("john.doe@example.com")
                .password("password123")
                .build();
        var tokenResp = new Token();
        tokenResp.setUserId("1");
        tokenResp.setAccessToken("Access_token");
        tokenResp.setRefreshToken("Refresh_Token");

        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken("john.doe@example.com", "password");
        when(userService.login(request)).thenReturn(mockAuthentication);
        when(tokenGenerator.createToken(mockAuthentication)).thenReturn(tokenResp);

        ResponseEntity<?> response = controller.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tokenResp, response.getBody());
        verify(userService, times(1)).login(request);
        verify(tokenGenerator, times(1)).createToken(mockAuthentication);
    }

    private static User getMockUser() {
        return User.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .accountLocked(false)
                .enabled(true)
                .deleted(false)
                .roles(Arrays.asList(Role.builder().id(1L).name("ROLE_USER").build(), Role.builder().id(2L).name("ROLE_ADMIN").build()))
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }
}