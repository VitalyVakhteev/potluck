package com.picnic.potluck.dto.user;

import com.picnic.potluck.util.Role;

import java.util.UUID;

public record UserDetail(
		UUID id,
		Role role,
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