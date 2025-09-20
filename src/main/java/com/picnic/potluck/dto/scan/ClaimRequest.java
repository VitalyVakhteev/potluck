package com.picnic.potluck.dto.scan;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ClaimRequest(
   @NotNull UUID fundraiserId,
   @NotNull UUID organizerId,
   long time,
   String signature
) {}
