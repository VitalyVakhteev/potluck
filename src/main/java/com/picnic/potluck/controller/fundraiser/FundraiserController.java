package com.picnic.potluck.controller.fundraiser;

import com.picnic.potluck.dto.fundraiser.*;
import com.picnic.potluck.service.fundraiser.FundraiserService;
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

    @PostMapping
    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    public ResponseEntity<CreateFundraiserResponse> createFundraiser(@AuthenticationPrincipal Jwt jwt,
                                                                     @RequestBody @Valid CreateFundraiserRequest req) {
        UUID organizerId = UUID.fromString(jwt.getSubject());
        var created = fundraiserService.add(organizerId, req);
        return ResponseEntity
                .created(URI.create("/api/fundraisers/" + created.id()))
                .body(created);
    }

    @PatchMapping
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public PatchFundraiserResponse editFundraiser(@AuthenticationPrincipal Jwt jwt,
                                                  @RequestBody @Valid PatchFundraiserRequest req) {
        UUID organizerId = UUID.fromString(jwt.getSubject());
        return fundraiserService.edit(organizerId, req);
    }

    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public DeleteFundraiserResponse deleteFundraiser(@AuthenticationPrincipal Jwt jwt,
                                                     @PathVariable UUID id) {
        UUID organizerId = UUID.fromString(jwt.getSubject());
        fundraiserService.delete(organizerId, id);
        return new DeleteFundraiserResponse(id);
    }
}