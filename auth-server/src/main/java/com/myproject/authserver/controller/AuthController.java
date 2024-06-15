package com.myproject.authserver.controller;

import com.myproject.authenticationcore.config.TokenGenerator;
import com.myproject.authserver.dto.LoginRequest;
import com.myproject.authserver.dto.RegistrationRequest;
import com.myproject.authserver.dto.UserDto;
import com.myproject.authserver.service.UserService;
import com.myproject.authserver.utils.DtoMapperUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final UserService userService;
    private final TokenGenerator tokenGenerator;

    @Operation(summary = "Register a new user", description = "Register a new user with the provided registration details")
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegistrationRequest registrationRequest) {
        return ResponseEntity.ok(DtoMapperUtil.toUserDto(userService.register(registrationRequest)));
    }

    @Operation(summary = "User login", description = "Authenticate a user with the provided login credentials")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws IOException {
        var authentication = userService.login(loginRequest);
        if (authentication != null) {
            return ResponseEntity.ok(tokenGenerator.createToken(authentication));
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
