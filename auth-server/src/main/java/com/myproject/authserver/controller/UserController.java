package com.myproject.authserver.controller;

import com.myproject.authserver.dto.UserDto;
import com.myproject.authserver.service.UserService;
import com.myproject.authserver.utils.DtoMapperUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "Operations related to user management")
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDto> userInfo(Authentication connectedUser) {
        return ResponseEntity.ok(DtoMapperUtil.toUserDto(userService.getUserInfo(connectedUser)));
    }


}
