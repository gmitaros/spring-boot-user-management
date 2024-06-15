package com.myproject.authserver.config;

import com.myproject.authenticationcore.model.AuthenticatedUser;
import com.myproject.authserver.model.Role;
import com.myproject.authserver.model.User;
import com.myproject.authserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Override
    @Transactional
    public AuthenticatedUser loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return AuthenticatedUser.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();
    }
}