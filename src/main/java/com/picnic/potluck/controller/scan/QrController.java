package com.picnic.potluck.controller.scan;

import com.picnic.potluck.dto.scan.QrResponse;
import com.picnic.potluck.repository.fundraiser.FundraiserRepository;
import com.picnic.potluck.service.scan.QrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class QrController {
    private final QrService qrService;
    private final FundraiserRepository fundraiserRepository;

    @Operation(
            summary = "Generate a QR Code.",
            description = "If a user is an organizer or an admin and the OP, create a QR code for a fundraiser.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Leaderboard row fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Illegal argument (Rewards are disabled)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request"),
            @ApiResponse(responseCode = "403", description = "Access Denied (Not the OP)"),
            @ApiResponse(responseCode = "404", description = "The given fundraiser was not found")
    })
    @Tag(name="QR/Points", description="QR/Points management API")
    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @GetMapping("/api/fundraisers/{id}/qr")
    public QrResponse qr(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        var organizerId = UUID.fromString(jwt.getSubject());
        var f = fundraiserRepository.findById(id).orElseThrow();
        if (!f.getOrganizer().getId().equals(organizerId)) throw new AccessDeniedException("Not your fundraiser");
        if (!f.isReward()) throw new IllegalArgumentException("Rewards for this fundraiser are currently disabled");
        return qrService.generateQr(organizerId, id);
    }
}
