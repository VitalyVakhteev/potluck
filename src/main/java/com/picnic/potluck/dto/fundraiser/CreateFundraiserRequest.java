package com.picnic.potluck.dto.fundraiser;

import jakarta.validation.constraints.*;

import java.time.Instant;

public record CreateFundraiserRequest(
		@NotBlank @Size(max = 80) String title,
		@Size(max = 500) String description,
		@Email @Size(max = 254) String email,
		@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$") String phoneNumber,
		@NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,
		@NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double lon,
		@NotNull Boolean reward,
		Instant startsAt,
		Instant endsAt
) {
}