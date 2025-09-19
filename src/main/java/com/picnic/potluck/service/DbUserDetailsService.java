package com.picnic.potluck.service;

import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {
    private final UserRepository users;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        var user = users.findByUsernameIgnoreCase(usernameOrEmail)
                .or(() -> users.findByEmailIgnoreCase(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return toPrincipal(user);
    }

    public static UserDetails toPrincipal(User user) {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        var pwd = user.getPasswordHash();
        if (pwd == null) {
            pwd = "{noop}__DISABLED__:" + UUID.randomUUID();
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(pwd)
                .authorities(authorities)
                .accountLocked(!user.isActive())
                .build();
    }
}