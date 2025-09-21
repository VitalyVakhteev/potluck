package com.picnic.potluck.dto.user;

import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String username,
        String firstName,
        String lastName,
        String bio,
        String location,
        String bannerColor,
        boolean displayName,
        boolean displayEmail,
        boolean displayPhone,
        int totalPoints
) {}