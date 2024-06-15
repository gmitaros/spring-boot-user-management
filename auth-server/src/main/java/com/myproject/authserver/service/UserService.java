package com.myproject.authserver.service;


import com.myproject.authenticationcore.config.TokenGenerator;
import com.myproject.authenticationcore.model.Token;
import com.myproject.authserver.dto.EmailDto;
import com.myproject.authserver.dto.LoginRequest;
import com.myproject.authserver.dto.RegistrationRequest;
import com.myproject.authserver.dto.enums.EmailTemplate;
import com.myproject.authserver.exceptions.GenericException;
import com.myproject.authserver.model.Role;
import com.myproject.authserver.model.User;
import com.myproject.authserver.repository.RoleRepository;
import com.myproject.authserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static com.myproject.authserver.dto.enums.BusinessErrorCodes.USER_EXISTS;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    @Value("${email.validation.enabled:false}")
    private boolean emailValidationEnabled;
//    @Value("${application.mailing.frontend.activation-url}")
//    private String activationUrl;
//    @Value("${application.activation.code.length:6}")
//    private int activationCodeLength;
//    @Value("${application.activation.code.valid.minutes:15}")
//    private int activationCodeMinutesValidity;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final WebClient webClient;
    private final TokenGenerator tokenGenerator;


    /**
     * Registers a new user
     *
     * @param request the registration request
     * @return the registration response
     */
    @Transactional
    public User register(RegistrationRequest request) {
        var userRole = getUserRole();
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new GenericException(USER_EXISTS);
        }
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(!emailValidationEnabled)
                .roles(List.of(userRole))
                .createdDate(LocalDateTime.now())
                .build();
        var storedUser = userRepository.save(user);
//        var newToken = generateAndSaveActivationToken(user);
        sendValidationEmail(storedUser);
        return storedUser;
    }

    /**
     * Sends a validation email
     *
     * @param user the user
     */
    private void sendValidationEmail(User user) {
        if (emailValidationEnabled) {
            logger.info("Sending activation email to {} for user {}", user.getEmail(), user.getId());

            final var email = EmailDto.builder()
                    .userId(user.getId())
                    .emailTemplate(EmailTemplate.ACTIVATE_ACCOUNT)
                    .recipientEmail(user.getEmail())
                    .message("test")
                    .subject("Welcome to My Project").build();

            Token token = tokenGenerator.createToken(SecurityContextHolder.getContext().getAuthentication());
            webClient.post()
                    .uri("/emails/send")
                    .bodyValue(email)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", "Bearer " + token.getAccessToken())
                    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .retrieve()
                    .toEntity(String.class)
                    .subscribe(
                            responseEntity -> {
                                logger.info("Welcome email has been send with success for user {}", user.getEmail());
                            },
                            error -> {
                                if (error instanceof WebClientResponseException) {
                                    WebClientResponseException ex = (WebClientResponseException) error;
                                    HttpStatusCode status = ex.getStatusCode();
                                    logger.error("Welcome email send failed with Error Status Code: {}", status.value(), error);
                                } else {
                                    logger.error("An unexpected error occurred on Welcome email send: {}", error.getMessage());
                                }
                            }
                    );

//            emailService.sendEmail(
//                    user.getEmail(),
//                    String.format("%s %s", user.getFirstname(), user.getLastname()),
//                    EmailTemplate.ACTIVATE_ACCOUNT,
//                    String.format("%s?activation-code=%s", activationUrl, token),
//                    "Account activation"
//            );
        }
    }


    /**
     * Retrieves the user role
     *
     * @return the user role
     */
    private Role getUserRole() {
        return roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Role USER was not initiated"));
    }

    /**
     * Authenticates a user
     *
     * @param request the login request
     * @return the login response
     */
    public Authentication login(LoginRequest request) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            return auth;
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for email: {}", request.getEmail(), e);
            throw new BadCredentialsException("Invalid credentials");
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for email: {}", request.getEmail(), e);
            throw new BadCredentialsException("Authentication failed");
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}