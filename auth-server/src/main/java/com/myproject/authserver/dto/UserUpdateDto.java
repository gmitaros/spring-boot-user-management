package com.myproject.authserver.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserUpdateDto {
    private String firstname;
    private String lastname;
    private String email;
    private boolean accountLocked;
    private boolean enabled;
    private List<String> roles;
}