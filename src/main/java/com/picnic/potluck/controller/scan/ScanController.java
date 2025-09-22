package com.picnic.potluck.controller.scan;

import com.picnic.potluck.dto.scan.ClaimRequest;
import com.picnic.potluck.dto.scan.ClaimResponse;
import com.picnic.potluck.service.scan.ScanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/scans")
@RequiredArgsConstructor
public class ScanController {
    private final ScanService scanService;

    @Operation(
            summary = "Claim points from a QR Code.",
            description = "If a user is a seeker or an admin, check the expiry and signature, then award points.",
            security = { @SecurityRequirement(name = "Bearer Authentication") }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Points awarded successfully"),
            @ApiResponse(responseCode = "400", description = "Illegal argument (Expired QR code, invalid signature)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request"),
            @ApiResponse(responseCode = "404", description = "The given fundraiser, fundraiser's organizer, or participant was not found")
    })
    @Tag(name="QR/Points", description="QR/Points management API")
    @PreAuthorize("hasAnyRole('SEEKER', 'ADMIN')")
    @PostMapping("/claim")
    public ClaimResponse claim(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid ClaimRequest req) {
        var participantId = UUID.fromString(jwt.getSubject());
        return scanService.claim(participantId, req);
    }
}
