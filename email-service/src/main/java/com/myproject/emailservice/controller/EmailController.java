package com.myproject.emailservice.controller;

import com.myproject.emailservice.dto.EmailDto;
import com.myproject.emailservice.model.Email;
import com.myproject.emailservice.service.EmailService;
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
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<Email> sendEmail(@RequestBody EmailDto email) {
        emailService.sendEmail(email);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Email>> getEmailsByUserId(@PathVariable Long userId) {
        List<Email> emails = emailService.getEmailsByUserId(userId);
        return new ResponseEntity<>(emails, HttpStatus.OK);
    }
}
