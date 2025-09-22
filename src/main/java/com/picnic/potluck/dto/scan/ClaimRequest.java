package com.picnic.potluck.dto.scan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ClaimRequest(
		@NotNull UUID fundraiserId,
		@NotNull UUID organizerId,
		@NotNull long time,
		@NotBlank String signature
) {
}
