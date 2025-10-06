package com.picnic.potluck.controller.user;

import com.picnic.potluck.dto.fundraiser.FundraiserSummary;
import com.picnic.potluck.dto.user.FavoriteRequest;
import com.picnic.potluck.dto.user.FavoriteResponse;
import com.picnic.potluck.repository.user.UserRepository;
import com.picnic.potluck.service.fundraiser.FundraiserQueryService;
import com.picnic.potluck.service.user.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/favorites")
@RequiredArgsConstructor
public class FavoriteController {
	private final FavoriteService favoriteService;
	private final FundraiserQueryService fundraisers;
	private final UserRepository userRepository;

	@Operation(
			summary = "Favorite.",
			description = "Favorite the requested fundraiser.",
			security = {@SecurityRequirement(name = "Bearer Authentication")}
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Favorited successfully"),
			@ApiResponse(responseCode = "400", description = "Illegal argument"),
			@ApiResponse(responseCode = "401", description = "Unauthorized request"),
			@ApiResponse(responseCode = "404", description = "The given fundraiser was not found")
	})
	@Tag(name = "Favorites", description = "Favorites management API")
	@PostMapping
	public FavoriteResponse addBody(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid FavoriteRequest req) {
		return favoriteService.add(UUID.fromString(jwt.getSubject()), req.fundraiserId());
	}

	@Operation(
			summary = "Unfavorite.",
			description = "Unfavorite the requested fundraiser.",
			security = {@SecurityRequirement(name = "Bearer Authentication")}
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Unfavorited successfully"),
			@ApiResponse(responseCode = "400", description = "Illegal argument"),
			@ApiResponse(responseCode = "401", description = "Unauthorized request"),
			@ApiResponse(responseCode = "404", description = "The given fundraiser was not found")
	})
	@Tag(name = "Favorites", description = "Favorites management API")
	@DeleteMapping
	public FavoriteResponse removeBody(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid FavoriteRequest req) {
		return favoriteService.remove(UUID.fromString(jwt.getSubject()), req.fundraiserId());
	}

	// 9-20-2025 3 AM I really don't like mixing services, but it's 3 am and I would like to sleep :)
	// 9-20-2025 6 PM I don't know what I was complaining about; this is better than directly using the FavoriteRepo.
	@Operation(
			summary = "Fetch favorites",
			description = "Get logged in user's favorites.",
			security = {@SecurityRequirement(name = "Bearer Authentication")}
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Fetched successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized request")
	})
	@Tag(name = "Favorites", description = "Favorites management API")
	@GetMapping
	public Page<FundraiserSummary> myFavorites(@AuthenticationPrincipal Jwt jwt,
											   @PageableDefault(
													   size = 20,
													   sort = "createdAt",
													   direction = Sort.Direction.DESC
											   ) Pageable p) {
		var userId = UUID.fromString(jwt.getSubject());
		var user = userRepository.findById(userId).orElseThrow();
		return fundraisers.list(user.getUsername(), p);
	}

	@Operation(
			summary = "Fetch favorites of user",
			description = "Get requested user's favorites.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Fetched successfully")
	})
	@Tag(name = "Favorites", description = "Favorites management API")
	@GetMapping("/{username}")
	public Page<FundraiserSummary> favoritesOf(@PathVariable String username,
											   @PageableDefault(
													   size = 20,
													   sort = "createdAt",
													   direction = Sort.Direction.DESC
											   ) Pageable p) {
		return fundraisers.list(username, p);
	}
}
