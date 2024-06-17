package com.myproject.emailservice.dto;

import com.myproject.emailservice.dto.enums.EmailStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailResponseDto {

    private Long id;
    private Long userId;
    private String subject;
    private String message;
    private String recipientEmail;
    private LocalDateTime sentAt;
    private EmailStatus status;

}
