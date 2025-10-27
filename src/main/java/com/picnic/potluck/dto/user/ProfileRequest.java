package com.picnic.potluck.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
		@Size(max = 50, message = "First name must be at most {max} characters") String firstName,
		@Size(max = 50, message = "Last name must be at most {max} characters") String lastName,
		@Size(max = 160, message = "Bio must be at most {max} characters") String bio,
		@Size(max = 120, message = "Location must be at most {max} characters") String location,
		@Pattern(regexp = "^$|^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Banner color must be a hex color like #0ea5e9") String bannerColor,
		Boolean displayName,
		Boolean displayEmail,
		Boolean displayPhone
) {
}