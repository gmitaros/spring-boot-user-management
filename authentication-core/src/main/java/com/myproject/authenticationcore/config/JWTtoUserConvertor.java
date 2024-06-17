package com.myproject.authenticationcore.config;


import com.myproject.authenticationcore.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JWTtoUserConvertor implements Converter<Jwt, UsernamePasswordAuthenticationToken> {


    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt source) {
        List<String> roles = Arrays.asList(((String) source.getClaims().get("roles")).split(" "));
        AuthenticatedUser user = new AuthenticatedUser(Long.valueOf(source.getSubject()), String.valueOf(source.getClaims().get("email")), "pass", roles);
        return new UsernamePasswordAuthenticationToken(user, source, user.getAuthorities());
    }


} 