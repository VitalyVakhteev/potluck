package com.picnic.potluck.dto.user;

import java.util.UUID;

public record FavoriteResponse(
		UUID userId,
		UUID fundraiserId,
		boolean favorited
) {
}