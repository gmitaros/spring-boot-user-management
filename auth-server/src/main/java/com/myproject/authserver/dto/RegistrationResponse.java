package com.myproject.authserver.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class RegistrationResponse {

    private String message;
    private Long userId;

}