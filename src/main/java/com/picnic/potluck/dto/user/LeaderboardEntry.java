package com.picnic.potluck.dto.user;

import java.util.UUID;

public record LeaderboardEntry(
		UUID userId,
		String username,
		long totalPoints,
		long rank
) {
}