package com.picnic.potluck.dto.auth;

import com.picnic.potluck.util.Role;
import jakarta.validation.constraints.*;

public record SignupRequest(
        @NotBlank @Size(min=3, max=40) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(max=20) String phone,
        @NotBlank @Size(min=8, max=100) String password,
        @NotNull Role role
) {}