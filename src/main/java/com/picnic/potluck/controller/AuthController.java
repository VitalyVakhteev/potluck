package com.picnic.potluck.controller;

import com.picnic.potluck.service.OidcService;
import com.picnic.potluck.service.UserService;
import com.picnic.potluck.dto.SignupRequest;
import com.picnic.potluck.dto.LoginRequest;
import com.picnic.potluck.dto.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

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
        return oidcService.fromGoogle(oidcUser);
    }
}