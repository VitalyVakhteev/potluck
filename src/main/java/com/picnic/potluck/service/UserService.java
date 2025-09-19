package com.picnic.potluck.service;

import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.UserRepository;
import com.picnic.potluck.security.JwtService;
import com.picnic.potluck.util.Role;
import com.picnic.potluck.web.dto.AuthResponse;
import com.picnic.potluck.web.dto.LoginRequest;
import com.picnic.potluck.web.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwt;

    @Transactional
    public AuthResponse signup(SignupRequest req) {
        users.findByEmailIgnoreCase(req.email()).ifPresent(u -> { throw new IllegalArgumentException("Email in use"); });
        users.findByUsernameIgnoreCase(req.username()).ifPresent(u -> { throw new IllegalArgumentException("Username in use"); });

        var user = User.builder()
                .username(req.username())
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .role(Role.SEEKER) // Todo: allow role selection
                .active(true)
                .build();

        users.save(user);
        var token = jwt.issue(user);
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        var auth = new UsernamePasswordAuthenticationToken(req.usernameOrEmail(), req.password());
        authenticationManager.authenticate(auth);

        var user = users.findByUsernameIgnoreCase(req.usernameOrEmail())
                .or(() -> users.findByEmailIgnoreCase(req.usernameOrEmail()))
                .orElseThrow();

        var token = jwt.issue(user);
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }
}
