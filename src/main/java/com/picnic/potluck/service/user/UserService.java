package com.picnic.potluck.service.user;

import com.picnic.potluck.dto.auth.AuthResponse;
import com.picnic.potluck.dto.auth.LoginRequest;
import com.picnic.potluck.dto.auth.SignupRequest;
import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.user.UserRepository;
import com.picnic.potluck.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        users.findByPhoneNumberIgnoreCase(req.phone()).ifPresent(u -> { throw new IllegalArgumentException("Phone number in use"); });

        var user = User.builder()
                .username(req.username())
                .email(req.email())
                .phoneNumber(req.phone())
                .passwordHash(passwordEncoder.encode(req.password()))
                .role(req.role())
                .active(true)
                .build();

        users.save(user);
        var token = jwt.issue(user);
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        var auth = new UsernamePasswordAuthenticationToken(req.email(), req.password());
        authenticationManager.authenticate(auth);

        var user = users.findByEmailIgnoreCase(req.email())
                .orElseThrow();

        var token = jwt.issue(user);
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }
}
