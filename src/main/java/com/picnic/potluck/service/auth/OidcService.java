package com.picnic.potluck.service.auth;

import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.user.UserRepository;
import com.picnic.potluck.security.JwtService;
import com.picnic.potluck.util.Role;
import com.picnic.potluck.dto.auth.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OidcService {
    private final UserRepository users;
    private final JwtService jwt;

    @Transactional
    public AuthResponse fromGoogle(OidcUser oidc) {
        var email = oidc.getEmail();
        var username = oidc.getPreferredUsername() != null ? oidc.getPreferredUsername() : email;

        var user = users.findByEmailIgnoreCase(email).orElseGet(() -> {
            var u = User.builder()
                    .username(username)
                    .email(email)
                    .role(Role.SEEKER)
                    .active(true)
                    .build();
            return users.save(u);
        });

        var token = jwt.issue(user);
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }
}
