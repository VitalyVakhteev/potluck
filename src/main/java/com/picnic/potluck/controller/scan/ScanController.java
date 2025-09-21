package com.picnic.potluck.controller.scan;

import com.picnic.potluck.dto.scan.ClaimRequest;
import com.picnic.potluck.dto.scan.ClaimResponse;
import com.picnic.potluck.service.scan.ScanService;
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

    @PreAuthorize("hasAnyRole('SEEKER', 'ADMIN')")
    @PostMapping("/claim")
    public ClaimResponse claim(@AuthenticationPrincipal Jwt jwt, @RequestBody ClaimRequest req) {
        var participantId = UUID.fromString(jwt.getSubject());
        return scanService.claim(participantId, req);
    }
}
