package com.picnic.potluck.dto.fundraiser;

import java.time.Instant;
import java.util.UUID;

public record FundraiserSummary(
		UUID id,
		String title,
		boolean active,
		Double lat,
		Double lon,
		Instant startsAt,
		Instant endsAt,
		String organizerUsername
) {
}