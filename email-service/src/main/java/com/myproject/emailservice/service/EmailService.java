package com.myproject.emailservice.service;

import com.myproject.emailservice.dto.EmailDto;
import com.myproject.emailservice.dto.enums.EmailStatus;
import com.myproject.emailservice.model.Email;
import com.myproject.emailservice.repository.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    @Value("${my.project.email.sender}")
    private String emailFromAddress;

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

    /**
     * Send an email
     *
     * @param emailDto
     */
    @Async
    public void sendEmail(EmailDto emailDto) {
        String template;
        switch (emailDto.getEmailTemplate()) {
            case ACTIVATE_ACCOUNT:
                template = buildConfirmEmailTemplate(emailDto.getRecipientEmail(), "confirmationUrl");
                break;
            default:
                throw new IllegalArgumentException("Invalid email template");
        }
        Email storedEmail = emailRepository.save(Email.builder()
                .userId(emailDto.getUserId())
                .subject(emailDto.getSubject())
                .message(template)
                .recipientEmail(emailDto.getRecipientEmail())
                .sentAt(LocalDateTime.now())
                .status(EmailStatus.PENDING)
                .build());
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MULTIPART_MODE_MIXED,
                    UTF_8.name()
            );
            helper.setFrom(emailFromAddress);
            helper.setTo(emailDto.getRecipientEmail());
            helper.setSubject(emailDto.getSubject());
            helper.setText(template, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            storedEmail.setStatus(EmailStatus.FAILED);
            emailRepository.save(storedEmail);
            throw new RuntimeException(e);
        }
        storedEmail.setStatus(EmailStatus.SENT);
        emailRepository.save(storedEmail);
    }

    /**
     * Build the email template for confirming the email address
     *
     * @param username        the username of the recipient
     * @param confirmationUrl the confirmation URL
     * @return the email template
     */
    private String buildConfirmEmailTemplate(String username, String confirmationUrl) {
        return String.format(
                "<html><body>" +
                        "<p>Hello %s,</p>" +
                        "<p>Thank you for registering on My Project. Please click on the following link to confirm your email address:</p>" +
                        "<a href=\"%s\">Confirm Email</a>" +
                        "<p>If the button doesn't work click here: %s</p>" +
                        "</body></html>",
                username, confirmationUrl, confirmationUrl
        );
    }


    public List<Email> getEmailsByUserId(Long userId) {
        return emailRepository.findByUserId(userId);
    }
}