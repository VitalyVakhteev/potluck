package com.picnic.potluck.dto.scan;

public record QrResponse(
		String claimUrl,
		long expiresAtEpochSeconds
) {
}