package com.picnic.potluck.dto.user;

import java.util.UUID;

public record UserSummary(
        UUID id,
        String username,
        int totalPoints,
        int totalFundraisers
) {}