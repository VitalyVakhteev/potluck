package com.picnic.potluck.controller.fundraiser;

import com.picnic.potluck.dto.fundraiser.FundraiserDetail;
import com.picnic.potluck.dto.fundraiser.FundraiserSummary;
import com.picnic.potluck.dto.fundraiser.NearRequest;
import com.picnic.potluck.service.fundraiser.FundraiserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/fundraisers")
@RequiredArgsConstructor
public class FundraiserQueryController {

    private final FundraiserQueryService fundraiserQueryService;

    // like that one british dnb geezer
    @GetMapping("/feed/me")
    public Page<FundraiserSummary> myFeed(@AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        return fundraiserQueryService.feed(UUID.fromString(jwt.getSubject()), pageable);
    }

    @GetMapping("/near")
    public Page<FundraiserSummary> near(@RequestBody NearRequest req,
                                        @PageableDefault(size = 20) Pageable pageable) {
        return fundraiserQueryService.near(req, pageable);
    }

    @GetMapping("/starting-soon")
    public Page<FundraiserSummary> startingSoon(@PageableDefault(size=20) Pageable pageable) {
        return fundraiserQueryService.startingSoon(pageable);
    }
    @GetMapping("/ending-soon")
    public Page<FundraiserSummary> endingSoon(@PageableDefault(size=20) Pageable pageable) {
        return fundraiserQueryService.endingSoon(pageable);
    }

    @GetMapping
    public Page<FundraiserSummary> listActive(@PageableDefault(size = 20, sort = "createdAt",
            direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return fundraiserQueryService.listActive(pageable);
    }

    @GetMapping("/search")
    public Page<FundraiserSummary> search(@RequestParam String q, @PageableDefault(size = 20) Pageable pageable) {
        return fundraiserQueryService.search(q, pageable);
    }

    @GetMapping("/{id}")
    public FundraiserDetail getOne(@PathVariable UUID id) {
        return fundraiserQueryService.getOne(id);
    }

    @GetMapping("/organizer/{organizerId}")
    public Page<FundraiserSummary> byOrganizer(@PathVariable UUID organizerId,
                                               @PageableDefault(size = 20, sort = "createdAt",
                                                       direction = org.springframework.data.domain.Sort.Direction.DESC)
                                               Pageable pageable) {
        return fundraiserQueryService.listByOrganizer(organizerId, pageable);
    }
}
