package com.picnic.potluck.dto.scan;

public record ClaimResponse(
        boolean duplicate,
        int pointsAwardedEach,
        String status
) {}