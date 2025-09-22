package com.picnic.potluck.controller.auth;

import com.picnic.potluck.service.auth.OidcService;
import com.picnic.potluck.service.user.UserService;
import com.picnic.potluck.dto.auth.SignupRequest;
import com.picnic.potluck.dto.auth.LoginRequest;
import com.picnic.potluck.dto.auth.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Operation(
            summary = "Sign up a new user.",
            description = "Allows SEEKER or ORGANIZER roles. Creates a user and returns its JWT token and some details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Illegal argument (user, email, or phone in use, or bad role")
    })
    @Tag(name="Auth", description="Auth management API")
    @PostMapping("/signup")
    public AuthResponse signup(@RequestBody @Valid SignupRequest req) {
        return userService.signup(req);
    }

    @Operation(
            summary = "Login with credentials.",
            description = "Logs in with a username and password, issuing and returning a JWT token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "404", description = "The user with those credentials was not found")
    })
    @Tag(name="Auth", description="Auth management API")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest req) {
        return userService.login(req);
    }

    @Operation(
            summary = "Login through OpenID.",
            description = "Logs in with a Google Oidc User, issuing and returning a JWT token.",
            security = { @SecurityRequirement(name = "Bearer Authentication") })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    @Tag(name="Auth", description="Auth management API")
    @GetMapping("/oidc/me")
    public AuthResponse oidcMe(@AuthenticationPrincipal OidcUser oidcUser) {
        return oidcService.fromGoogle(oidcUser);
    }
}