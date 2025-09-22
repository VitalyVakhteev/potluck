package com.picnic.potluck.dto.fundraiser;

import jakarta.validation.constraints.NotNull;

public record NearRequest(
		@NotNull Double lat,
		@NotNull Double lon,
		@NotNull Double radiusKm
) {
}
