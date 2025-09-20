package com.picnic.potluck.dto.user;

import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String username,
        String bio,
        String location,
        String bannerColor,
        boolean displayEmail,
        boolean displayPhone,
        int totalPoints
) {}