package com.picnic.potluck.controller.user;

import com.picnic.potluck.dto.user.ProfileRequest;
import com.picnic.potluck.dto.user.ProfileResponse;
import com.picnic.potluck.dto.user.UserDetail;
import com.picnic.potluck.dto.user.UserSummary;
import com.picnic.potluck.service.user.ProfileService;
import com.picnic.potluck.service.user.UserQueryService;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserQueryService userQueryService;
	private final ProfileService profileService;

	@Operation(
			summary = "Search for users.",
			description = "Return a pageable object of users if the keyword matches certain fields.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Fetched successfully")
	})
	@Tag(name = "User", description = "User management API")
	@GetMapping("/search")
	public Page<UserSummary> search(@RequestParam String q,
									@PageableDefault(
											size = 20,
											sort = "createdAt",
											direction = Sort.Direction.DESC
									) Pageable pageable) {
		return userQueryService.search(q, pageable);
	}

	@Operation(
			summary = "Get a user.",
			description = "Return a user based on their id.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Fetched successfully"),
			@ApiResponse(responseCode = "404", description = "User not found")
	})
	@Tag(name = "User", description = "User management API")
	@GetMapping("/id/{id}")
	public UserDetail getUser(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
		UUID viewer = (jwt == null) ? null : UUID.fromString(jwt.getSubject());
		return userQueryService.getUserForViewer(id, viewer);
	}

	@Operation(
			summary = "Get a user's summary.",
			description = "Return a user's summary based on their id.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Fetched successfully"),
			@ApiResponse(responseCode = "404", description = "User not found")
	})
	@Tag(name = "User", description = "User management API")
	@GetMapping("/id/{id}/summary")
	public UserSummary summary(@PathVariable UUID id) {
		return userQueryService.getSummary(id);
	}

	@Operation(
			summary = "Get a user.",
			description = "Return a user based on their username.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Fetched successfully"),
			@ApiResponse(responseCode = "404", description = "User not found")
	})
	@Tag(name = "User", description = "User management API")
	@GetMapping("/u/{username}")
	public UserDetail getUser(@AuthenticationPrincipal Jwt jwt, @PathVariable String username) {
		UUID viewer = (jwt == null) ? null : UUID.fromString(jwt.getSubject());
		return userQueryService.getUserForViewer(username, viewer);
	}

	@Operation(
			summary = "Get oneself.",
			description = "Return oneself.",
			security = {@SecurityRequirement(name = "Bearer Authentication")}
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Fetched successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized request"),
			@ApiResponse(responseCode = "404", description = "User not found (if this triggers, this is bad; auth check failed)")
	})
	@Tag(name = "User", description = "User management API")
	@GetMapping("/me")
	public UserDetail me(@AuthenticationPrincipal Jwt jwt) {
		if (jwt == null) {
			throw new ResponseStatusException(
					HttpStatus.UNAUTHORIZED);
		}
		UUID me = UUID.fromString(jwt.getSubject());
		return userQueryService.getUserForViewer(me, me);
	}

	@Operation(
			summary = "Edit oneself.",
			description = "Edit the profile of oneself.",
			security = {@SecurityRequirement(name = "Bearer Authentication")}
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Patched successfully"),
			@ApiResponse(responseCode = "400", description = "Illegal argument"),
			@ApiResponse(responseCode = "401", description = "Unauthorized request"),
			@ApiResponse(responseCode = "404", description = "User not found (if this triggers, this is bad; auth check failed)")
	})
	@Tag(name = "User", description = "User management API")
	@PatchMapping("/me")
	public ProfileResponse modifyProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid ProfileRequest req) {
		if (jwt == null) {
			throw new ResponseStatusException(
					HttpStatus.UNAUTHORIZED);
		}
		UUID userId = UUID.fromString(jwt.getSubject());
		return profileService.modifyProfile(userId, req);
	}
}
