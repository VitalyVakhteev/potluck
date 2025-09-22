package com.picnic.potluck.dto.user;

import java.util.UUID;

public record UserDetail(
		UUID id,
		String username,
		String bio,
		String location,
		String bannerColor,
		int totalPoints,
		int totalFundraisers,
		long followersCount,
		long followingCount,
		long favoritesCount,
		String firstName,
		String lastName,
		String email,
		String phoneNumber,
		boolean displayName,
		boolean displayEmail,
		boolean displayPhone
) {
}