package com.myproject.emailservice.controller;

import com.myproject.emailservice.dto.EmailDto;
import com.myproject.emailservice.model.Email;
import com.myproject.emailservice.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
@Tag(name = "Emails", description = "Operations related to email management")
public class EmailController {

    private final EmailService emailService;

    /**
     * Sends an email.
     *
     * @param email the email details
     * @return HTTP status 201 Created
     */
    @Operation(summary = "Send an email", description = "Send an email with the provided details")
    @PostMapping("/send")
    public ResponseEntity<Email> sendEmail(@RequestBody EmailDto email) {
        emailService.sendEmail(email);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Retrieves all emails sent to a specific user.
     *
     * @param userId the ID of the user
     * @return a list of emails sent to the user
     */
    @Operation(summary = "Get emails by user ID", description = "Retrieve all emails sent to a specific user by user ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Email>> getEmailsByUserId(@PathVariable Long userId) {
        List<Email> emails = emailService.getEmailsByUserId(userId);
        return new ResponseEntity<>(emails, HttpStatus.OK);
    }
}
