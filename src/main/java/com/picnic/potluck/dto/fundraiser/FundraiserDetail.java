package com.picnic.potluck.dto.fundraiser;

import java.time.Instant;
import java.util.UUID;

public record FundraiserDetail(
		UUID id,
		String title,
		String description,
		String email,
		String phoneNumber,
		boolean active,
		Double lat,
		Double lon,
		Instant startsAt,
		Instant endsAt,
		UUID organizerId,
		String organizerUsername
) {
}