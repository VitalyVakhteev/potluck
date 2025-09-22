package com.picnic.potluck.dto.fundraiser;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public record PatchFundraiserRequest(
        UUID id,
        @Size(max = 80) String title,
        @Size(max = 500) String description,
        @Email @Size(max = 254) String email,
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$") String phoneNumber,
        @DecimalMin("-90.0")  @DecimalMax("90.0")   Double lat,
        @DecimalMin("-180.0") @DecimalMax("180.0") Double lon,
        Boolean reward,
        Instant startsAt,
        Instant endsAt
) {}