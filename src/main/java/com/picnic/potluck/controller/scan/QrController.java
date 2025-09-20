package com.picnic.potluck.controller.scan;

import com.picnic.potluck.dto.scan.QrResponse;
import com.picnic.potluck.repository.fundraiser.FundraiserRepository;
import com.picnic.potluck.service.scan.QrService;
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

    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @GetMapping("/api/fundraisers/{id}/qr")
    public QrResponse qr(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        var organizerId = UUID.fromString(jwt.getSubject());
        var f = fundraiserRepository.findById(id).orElseThrow();
        if (!f.getOrganizer().getId().equals(organizerId)) throw new AccessDeniedException("Not your fundraiser");
        return qrService.generateQr(organizerId, id);
    }
}
