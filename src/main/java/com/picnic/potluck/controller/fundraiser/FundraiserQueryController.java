package com.picnic.potluck.controller.fundraiser;

import com.picnic.potluck.dto.fundraiser.FundraiserDetail;
import com.picnic.potluck.dto.fundraiser.FundraiserSummary;
import com.picnic.potluck.service.fundraiser.FundraiserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/fundraisers")
@RequiredArgsConstructor
public class FundraiserQueryController {

    private final FundraiserQueryService fundraiserQueryService;

    @GetMapping("/{id}")
    public FundraiserDetail getOne(@PathVariable UUID id) {
        return fundraiserQueryService.getOne(id);
    }

    @GetMapping
    public Page<FundraiserSummary> listActive(@PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
                                              Pageable pageable) {
        return fundraiserQueryService.listActive(pageable);
    }

    @GetMapping("/search")
    public Page<FundraiserSummary> search(@RequestParam String q,
                                          @PageableDefault(size = 20) Pageable pageable) {
        return fundraiserQueryService.search(q, pageable);
    }

    @GetMapping("/organizer/{organizerId}")
    public Page<FundraiserSummary> byOrganizer(@PathVariable UUID organizerId,
                                               @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
                                               Pageable pageable) {
        return fundraiserQueryService.listByOrganizer(organizerId, pageable);
    }

    // Todo: turn this into a DTO
    @GetMapping("/near")
    public Page<FundraiserSummary> near(@RequestParam double lat,
                                        @RequestParam double lon,
                                        @RequestParam double radiusKm,
                                        @PageableDefault(size = 20) Pageable pageable) {
        return fundraiserQueryService.near(lat, lon, radiusKm, pageable);
    }

    @GetMapping("/starting-soon")
    public Page<FundraiserSummary> startingSoon(@PageableDefault(size=20) Pageable pageable) {
        return fundraiserQueryService.startingSoon(pageable);
    }
    @GetMapping("/ending-soon")
    public Page<FundraiserSummary> endingSoon(@PageableDefault(size=20) Pageable pageable) {
        return fundraiserQueryService.endingSoon(pageable);
    }
}
