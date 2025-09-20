package com.picnic.potluck.dto.user;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FavoriteRequest(
        @NotNull UUID fundraiserId
) {}