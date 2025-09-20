package com.picnic.potluck.controller.auth;

import com.picnic.potluck.service.auth.OidcService;
import com.picnic.potluck.service.user.UserService;
import com.picnic.potluck.dto.auth.SignupRequest;
import com.picnic.potluck.dto.auth.LoginRequest;
import com.picnic.potluck.dto.auth.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final OidcService oidcService;

    @PostMapping("/signup")
    public AuthResponse signup(@RequestBody @Valid SignupRequest req) {
        return userService.signup(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest req) {
        return userService.login(req);
    }

    @GetMapping("/oidc/me")
    public AuthResponse oidcMe(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return oidcService.fromGoogle(oidcUser);
    }
}