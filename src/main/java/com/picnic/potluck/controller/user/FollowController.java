package com.picnic.potluck.controller.user;

import com.picnic.potluck.dto.user.FollowResponse;
import com.picnic.potluck.dto.user.UserSummary;
import com.picnic.potluck.service.user.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/follows")
@RequiredArgsConstructor
public class FollowController {
	private final FollowService followService;

	@Operation(
			summary = "Follow.",
			description = "Follow the requested user.",
			security = {@SecurityRequirement(name = "Bearer Authentication")}
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Followed successfully"),
			@ApiResponse(responseCode = "400", description = "Illegal argument (cannot follow self)"),
			@ApiResponse(responseCode = "401", description = "Unauthorized request")
	})
	@Tag(name = "Follows", description = "Follows management API")
	@PostMapping("/{targetId}")
	public FollowResponse follow(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID targetId) {
		return followService.follow(UUID.fromString(jwt.getSubject()), targetId);
	}

	@Operation(
			summary = "Unfollow.",
			description = "Unfollow the requested user.",
			security = {@SecurityRequirement(name = "Bearer Authentication")}
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Unfollowed successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized request")
	})
	@Tag(name = "Follows", description = "Follows management API")
	@DeleteMapping("/{targetId}")
	public FollowResponse unfollow(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID targetId) {
		return followService.unfollow(UUID.fromString(jwt.getSubject()), targetId);
	}

	@Operation(
			summary = "Get followers.",
			description = "Get the followers of the requested user.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Fetched successfully")
	})
	@Tag(name = "Follows", description = "Follows management API")
	@GetMapping("/followers/{userId}")
	public Page<UserSummary> followers(@PathVariable UUID userId,
									   @PageableDefault(
											   size = 20,
											   sort = "createdAt",
											   direction = Sort.Direction.DESC
									   ) Pageable p) {
		return followService.followers(userId, p);
	}

	@Operation(
			summary = "Get following.",
			description = "Get the users the requested user is following")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Fetched successfully")
	})
	@Tag(name = "Follows", description = "Follows management API")
	@GetMapping("/following/{userId}")
	public Page<UserSummary> following(@PathVariable UUID userId,
									   @PageableDefault(
											   size = 20,
											   sort = "createdAt",
											   direction = Sort.Direction.DESC
									   ) Pageable p) {
		return followService.following(userId, p);
	}

	@Operation(
			summary = "Get one's followers.",
			description = "Get the followers of oneself.",
			security = {@SecurityRequirement(name = "Bearer Authentication")}
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Fetched successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized request")
	})
	@Tag(name = "Follows", description = "Follows management API")
	@GetMapping("/followers/me")
	public Page<UserSummary> myFollowers(@AuthenticationPrincipal Jwt jwt,
										 @PageableDefault(
												 size = 20,
												 sort = "createdAt",
												 direction = Sort.Direction.DESC
										 ) Pageable p) {
		return followService.followers(UUID.fromString(jwt.getSubject()), p);
	}

	@Operation(
			summary = "Get one's following.",
			description = "Get the users being followed by oneself.",
			security = {@SecurityRequirement(name = "Bearer Authentication")}
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Fetched successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized request")
	})
	@Tag(name = "Follows", description = "Follows management API")
	@GetMapping("/following/me")
	public Page<UserSummary> myFollowing(@AuthenticationPrincipal Jwt jwt,
										 @PageableDefault(
												 size = 20,
												 sort = "createdAt",
												 direction = Sort.Direction.DESC
										 ) Pageable p) {
		return followService.following(UUID.fromString(jwt.getSubject()), p);
	}
}