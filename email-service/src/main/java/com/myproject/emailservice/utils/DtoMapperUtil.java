package com.myproject.emailservice.utils;

import com.myproject.emailservice.dto.EmailResponseDto;
import com.myproject.emailservice.model.Email;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapperUtil {

    public static EmailResponseDto toEmailDto(Email email) {
        return EmailResponseDto.builder()
                .id(email.getId())
                .userId(email.getUserId())
                .subject(email.getSubject())
                .message(email.getMessage())
                .recipientEmail(email.getRecipientEmail())
                .sentAt(email.getSentAt())
                .status(email.getStatus())
                .build();
    }
}
