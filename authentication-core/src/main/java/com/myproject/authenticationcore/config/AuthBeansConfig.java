package com.myproject.authenticationcore.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@RequiredArgsConstructor
public class AuthBeansConfig {

    private final KeyUtils keyUtils;

    @Bean
    public JwtEncoder jwtEncoder() throws Exception {
        RSAPrivateKey privateKey = keyUtils.getAccessTokenPrivateKey();
        RSAPublicKey publicKey = keyUtils.getAccessTokenPublicKey();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        com.nimbusds.jose.jwk.source.JWKSource<SecurityContext> jwkSource =
                (jwkSelector, context) -> jwkSelector.select(jwkSet);
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder customJwtDecoder() {
        RSAPublicKey publicKey = null;
        try {
            publicKey = keyUtils.getRefreshTokenPublicKey();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

}