package com.picnic.potluck.dto.user;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FollowResponse(
		@NotNull UUID userId,
		@NotNull UUID targetId,
		Boolean isFollowed
) {
}
