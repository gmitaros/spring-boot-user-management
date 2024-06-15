package com.myproject.authenticationcore.config;

import com.myproject.authenticationcore.model.AuthenticatedUser;
import com.myproject.authenticationcore.model.Token;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TokenGenerator {

    private final KeyUtils keyUtils;

    NimbusJwtEncoder jwtAccessTokenEncoder() throws IOException {
        JWK jwk = new RSAKey
                .Builder(keyUtils.getAccessTokenPublicKey())
                .privateKey(keyUtils.getAccessTokenPrivateKey())
                .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    NimbusJwtEncoder jwtRefreshTokenEncoder() throws IOException {
        JWK jwk = new RSAKey
                .Builder(keyUtils.getRefreshTokenPublicKey())
                .privateKey(keyUtils.getRefreshTokenPrivateKey())
                .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    private String createAccessToken(Authentication authentication) throws IOException {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Instant now = Instant.now();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("myApp")
                .issuedAt(now)
                .expiresAt(now.plus(5, ChronoUnit.MINUTES))
                .subject(String.valueOf(user.getId()))
                .claim("roles", authorities)
                .build();

        return jwtAccessTokenEncoder().encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    private String createRefreshToken(Authentication authentication) throws IOException {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Instant now = Instant.now();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("myApp")
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.DAYS))
                .subject(String.valueOf(user.getId()))
                .build();

        return jwtRefreshTokenEncoder().encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    public Token createToken(Authentication authentication) {
        try {
            if (!(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
                throw new BadCredentialsException(
                        MessageFormat.format("principal {0} is not of User type", authentication.getPrincipal().getClass())
                );
            }
            Token tokenDTO = new Token();
            tokenDTO.setUserId(String.valueOf(user.getId()));
            tokenDTO.setAccessToken(createAccessToken(authentication));

            String refreshToken;
            if (authentication.getCredentials() instanceof Jwt jwt) {
                Instant now = Instant.now();
                Instant expiresAt = jwt.getExpiresAt();
                Duration duration = Duration.between(now, expiresAt);
                long daysUntilExpired = duration.toDays();
                if (daysUntilExpired < 7) {
                    refreshToken = createRefreshToken(authentication);
                } else {
                    refreshToken = jwt.getTokenValue();
                }
            } else {
                refreshToken = createRefreshToken(authentication);
            }
            tokenDTO.setRefreshToken(refreshToken);

            return tokenDTO;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create access token");
        }
    }

} 
