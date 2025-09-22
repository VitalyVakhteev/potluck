package com.picnic.potluck.dto.fundraiser;

import java.time.Instant;
import java.util.UUID;

public record PatchFundraiserResponse(
		UUID id,
		String title,
		Instant startsAt,
		Instant endsAt
) {
}