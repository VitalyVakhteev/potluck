package com.picnic.potluck.controller.fundraiser;

import com.picnic.potluck.dto.fundraiser.FundraiserDetail;
import com.picnic.potluck.dto.fundraiser.FundraiserSummary;
import com.picnic.potluck.dto.fundraiser.NearRequest;
import com.picnic.potluck.service.fundraiser.FundraiserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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
    @Operation(
            summary = "Fetch your fundraiser feed.",
            description = "Any logged in user can fetch their feed. Returns the latest fundraisers from followed users.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feed fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    @Tag(name="Fundraiser", description="Fundraiser management API")
    @Cacheable(cacheNames = "followerFundraisers", unless = "#result.empty()")
    @GetMapping("/feed/me")
    public Page<FundraiserSummary> myFeed(@AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        return fundraiserQueryService.feed(UUID.fromString(jwt.getSubject()), pageable);
    }

    @Operation(
            summary = "Fetch nearby fundraisers.",
            description = "Within a certain radius of a lat/lon, returns the latest fundraisers.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fundraisers fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Illegal argument (invalid lat,lon,radius)")
    })
    @Tag(name="Fundraiser", description="Fundraiser management API")
    @GetMapping("/near")
    public Page<FundraiserSummary> near(@RequestBody @Valid NearRequest req,
                                        @PageableDefault(size = 20) Pageable pageable) {
        return fundraiserQueryService.near(req, pageable);
    }

    @Operation(
            summary = "Fetch fundraisers starting soon.",
            description = "Sorted by ascending delta from now to start, returns fundraisers.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fundraisers fetched successfully")
    })
    @Tag(name="Fundraiser", description="Fundraiser management API")
    @GetMapping("/starting-soon")
    public Page<FundraiserSummary> startingSoon(@PageableDefault(size=20) Pageable pageable) {
        return fundraiserQueryService.startingSoon(pageable);
    }

    @Operation(
            summary = "Fetch fundraisers ending soon.",
            description = "Sorted by ascending delta from now to end, returns fundraisers.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fundraisers fetched successfully")
    })
    @Tag(name="Fundraiser", description="Fundraiser management API")
    @GetMapping("/ending-soon")
    public Page<FundraiserSummary> endingSoon(@PageableDefault(size=20) Pageable pageable) {
        return fundraiserQueryService.endingSoon(pageable);
    }

    @Operation(
            summary = "Fetch latest fundraisers.",
            description = "Returns fundraisers by the latest creation date.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fundraisers fetched successfully")
    })
    @Tag(name="Fundraiser", description="Fundraiser management API")
    @GetMapping
    public Page<FundraiserSummary> listActive(@PageableDefault(size = 20, sort = "createdAt",
            direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return fundraiserQueryService.listActive(pageable);
    }

    @Operation(
            summary = "Fetch fundraisers by keyword.",
            description = "Returns fundraisers if certain terms match the keyword.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fundraisers fetched successfully")
    })
    @Tag(name="Fundraiser", description="Fundraiser management API")
    @Cacheable(cacheNames = "searchFundraisers", unless = "#result.empty()")
    @GetMapping("/search")
    public Page<FundraiserSummary> search(@RequestParam String q, @PageableDefault(size = 20) Pageable pageable) {
        return fundraiserQueryService.search(q, pageable);
    }

    @Operation(
            summary = "Get a specific fundraiser.",
            description = "Returns a fundraiser with a matching id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fundraisers fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Fundraiser not found")
    })
    @Tag(name="Fundraiser", description="Fundraiser management API")
    @GetMapping("/{id}")
    public FundraiserDetail getOne(@PathVariable UUID id) {
        return fundraiserQueryService.getOne(id);
    }

    @Operation(
            summary = "Get organizer's fundraisers.",
            description = "Returns an organizer's latest fundraisers by creation date.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fundraisers fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Organizer not found")
    })
    @Tag(name="Fundraiser", description="Fundraiser management API")
    @Cacheable(cacheNames = "organizerFundraisers", unless = "#result.empty()")
    @GetMapping("/organizer/{organizerId}")
    public Page<FundraiserSummary> byOrganizer(@PathVariable UUID organizerId,
                                               @PageableDefault(size = 20, sort = "createdAt",
                                                       direction = org.springframework.data.domain.Sort.Direction.DESC)
                                               Pageable pageable) {
        return fundraiserQueryService.listByOrganizer(organizerId, pageable);
    }
}
