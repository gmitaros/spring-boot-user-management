package com.myproject.authserver.config;

import com.myproject.authenticationcore.config.JWTtoUserConvertor;
import com.myproject.authenticationcore.config.KeyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BeansConfig {

    private final JWTtoUserConvertor jwttoUserConvertor;
    private final UserDetailsService userDetailsService;
    private final KeyUtils keyUtils;

    @Value("${security.allowed.origins}")
    private List<String> allowedOrigins;

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean("passwordEncoder")
    @Primary
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Qualifier("jwtRefreshTokenAuthProvider")
    JwtAuthenticationProvider jwtRefreshTokenAuthProvider() throws IOException {
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(NimbusJwtDecoder.withPublicKey(keyUtils.getRefreshTokenPublicKey()).build());
        provider.setJwtAuthenticationConverter(jwttoUserConvertor);
        return provider;
    }

//    @Bean
//    public CorsFilter corsFilter() {
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        final CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.setAllowedOrigins(allowedOrigins);
//        config.setAllowedHeaders(Arrays.asList(
//                HttpHeaders.ORIGIN,
//                HttpHeaders.CONTENT_TYPE,
//                HttpHeaders.ACCEPT,
//                HttpHeaders.AUTHORIZATION
//        ));
//        config.setAllowedMethods(Arrays.asList(
//                "GET",
//                "POST",
//                "DELETE",
//                "PUT",
//                "PATCH"
//        ));
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }

//    @Bean
////    CorsConfigurationSource corsConfigurationSource() {
////        CorsConfiguration configuration = new CorsConfiguration();
////        configuration.setAllowCredentials(true);
////        configuration.setAllowedOrigins(allowedOrigins);
////        configuration.setAllowedMethods(Arrays.asList(
////                "GET",
////                "POST",
////                "DELETE",
////                "PUT",
////                "PATCH"
////        ));
////        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////        source.registerCorsConfiguration("/**", configuration);
////        return source;
////    }

}