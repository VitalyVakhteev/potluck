package com.picnic.potluck.controller.user;

import com.picnic.potluck.dto.fundraiser.FundraiserSummary;
import com.picnic.potluck.dto.user.FavoriteRequest;
import com.picnic.potluck.dto.user.FavoriteResponse;
import com.picnic.potluck.service.fundraiser.FundraiserQueryService;
import com.picnic.potluck.service.user.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/me/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final FundraiserQueryService fundraisers;

    @PostMapping
    public FavoriteResponse addBody(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid FavoriteRequest req) {
        return favoriteService.add(UUID.fromString(jwt.getSubject()), req.fundraiserId());
    }

    @DeleteMapping
    public FavoriteResponse removeBody(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid FavoriteRequest req) {
        return favoriteService.remove(UUID.fromString(jwt.getSubject()), req.fundraiserId());
    }

    // I really don't likew mixing services but it's 3 am and I would like to sleep :)
    @GetMapping
    public Page<FundraiserSummary> myFavorites(@AuthenticationPrincipal Jwt jwt, Pageable p) {
        var userId = UUID.fromString(jwt.getSubject());
        return fundraisers.list(userId, p);
    }
}
