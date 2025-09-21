package com.picnic.potluck.dto.fundraiser;

import jakarta.validation.constraints.NotNull;

public record NearRequest(
        @NotNull double lat,
        @NotNull double lon,
        @NotNull double radiusKm
) {}
