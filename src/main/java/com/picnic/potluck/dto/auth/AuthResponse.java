package com.picnic.potluck.dto.auth;

import java.util.UUID;

public record AuthResponse(
		String token,
		UUID userId,
		String username,
		String role
) {
}