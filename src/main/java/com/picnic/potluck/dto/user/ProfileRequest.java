package com.picnic.potluck.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
		@Size(max = 50) String firstName,
		@Size(max = 50) String lastName,
		@Size(max = 160) String bio,
		@Size(max = 120) String location,
		@Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$") String bannerColor,
		Boolean displayName,
		Boolean displayEmail,
		Boolean displayPhone
) {
}