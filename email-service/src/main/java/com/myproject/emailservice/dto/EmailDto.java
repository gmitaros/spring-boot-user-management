package com.myproject.emailservice.dto;

import com.myproject.emailservice.dto.enums.EmailTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {

    private Long userId;
    private String subject;
    private String message;
    private String recipientEmail;
    private EmailTemplate emailTemplate;
}