package com.picnic.potluck.dto.scan;

public record ClaimResponse(
        boolean duplicate,
        boolean pointsAwarded,
        int pointsAwardedEach,
        String status
) {}