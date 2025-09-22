package com.picnic.potluck.controller.fundraiser;

import com.picnic.potluck.dto.fundraiser.*;
import com.picnic.potluck.service.fundraiser.FundraiserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/fundraisers")
@RequiredArgsConstructor
public class FundraiserController {

    private final FundraiserService fundraiserService;

    @Operation(
            summary = "Create a new fundraiser.",
            description = "ORGANIZER or ADMIN users can create a fundraiser. Returns a fundraiser with some basic details.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Fundraiser created successfully"),
            @ApiResponse(responseCode = "400", description = "Illegal argument (endsAt before startsAt)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request"),
            @ApiResponse(responseCode = "404", description = "The organizer user was not found")
    })
    @Tag(name="Fundraiser", description="Fundraiser management API")
    @PostMapping
    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    public CreateFundraiserResponse createFundraiser(@AuthenticationPrincipal Jwt jwt,
                                                     @RequestBody @Valid CreateFundraiserRequest req) {
        UUID organizerId = UUID.fromString(jwt.getSubject());
        return fundraiserService.add(organizerId, req);
    }

    @Operation(
            summary = "Edit a fundraiser.",
            description = "ORGANIZER or ADMIN users who are the OP can edit. Returns the patched details.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Fundraiser edited successfully"),
            @ApiResponse(responseCode = "400", description = "Illegal argument (endsAt before startsAt)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request"),
            @ApiResponse(responseCode = "403", description = "Forbidden request, not the OP"),
            @ApiResponse(responseCode = "404", description = "The specified fundraiser was not found")
    })
    @Tag(name="Fundraiser", description="Fundraiser management API")
    @PatchMapping
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public PatchFundraiserResponse editFundraiser(@AuthenticationPrincipal Jwt jwt,
                                                  @RequestBody @Valid PatchFundraiserRequest req) {
        UUID organizerId = UUID.fromString(jwt.getSubject());
        return fundraiserService.edit(organizerId, req);
    }

    @Operation(
            summary = "Delete a fundraiser.",
            description = "ORGANIZER or ADMIN users who are the OP can delete. Returns the deleted id.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Fundraiser deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request"),
            @ApiResponse(responseCode = "403", description = "Forbidden request, not the OP"),
            @ApiResponse(responseCode = "404", description = "The specified fundraiser was not found")
    })
    @Tag(name="Fundraiser", description="Fundraiser management API")
    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public DeleteFundraiserResponse deleteFundraiser(@AuthenticationPrincipal Jwt jwt,
                                                     @PathVariable UUID id) {
        UUID organizerId = UUID.fromString(jwt.getSubject());
        return fundraiserService.delete(organizerId, id);
    }
}