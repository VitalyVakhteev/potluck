package com.picnic.potluck.service.user;

import com.picnic.potluck.dto.auth.AuthResponse;
import com.picnic.potluck.dto.auth.LoginRequest;
import com.picnic.potluck.dto.auth.SignupRequest;
import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.user.UserRepository;
import com.picnic.potluck.security.JwtService;
import com.picnic.potluck.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwt;

    @Transactional
    public AuthResponse signup(SignupRequest req) {
        userRepository.findByEmailIgnoreCase(req.email()).ifPresent(u -> { throw new IllegalArgumentException("Email in use"); });
        userRepository.findByUsernameIgnoreCase(req.username()).ifPresent(u -> { throw new IllegalArgumentException("Username in use"); });
        userRepository.findByPhoneNumberIgnoreCase(req.phone()).ifPresent(u -> { throw new IllegalArgumentException("Phone number in use"); });
        Role final_role = req.role();
        if (final_role == Role.ADMIN) {
            // Todo: log here that this endpoint doesn't support admin signups. For now, we default to organizer.
            final_role = Role.ORGANIZER;
        }

        var user = User.builder()
                .username(req.username())
                .email(req.email())
                .phoneNumber(req.phone())
                .passwordHash(passwordEncoder.encode(req.password()))
                .role(final_role)
                .active(true)
                .build();

        userRepository.save(user);
        var token = jwt.issue(user);
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        var auth = new UsernamePasswordAuthenticationToken(req.username(), req.password());
        authenticationManager.authenticate(auth);

        var user = userRepository.findByUsernameIgnoreCase(req.username())
                .orElseThrow();

        var token = jwt.issue(user);
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }
}
