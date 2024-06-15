package com.myproject.authserver.controller;

import com.myproject.authenticationcore.config.TokenGenerator;
import com.myproject.authserver.dto.LoginRequest;
import com.myproject.authserver.dto.RegistrationRequest;
import com.myproject.authserver.model.User;
import com.myproject.authserver.service.UserService;
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
public class AuthController {

    private final UserService userService;
    private final TokenGenerator tokenGenerator;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegistrationRequest registrationRequest) {
        return ResponseEntity.ok(userService.register(registrationRequest));
    }

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
