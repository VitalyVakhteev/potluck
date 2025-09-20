package com.picnic.potluck.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @Size(max = 160) String bio,
        @Size(max = 120) String location,
        @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$") String bannerColor,
        Boolean displayEmail,
        Boolean displayPhone
) {}