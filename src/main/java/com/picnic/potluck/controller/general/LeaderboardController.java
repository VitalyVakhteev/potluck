package com.picnic.potluck.controller.general;

import com.picnic.potluck.dto.user.LeaderboardEntry;
import com.picnic.potluck.service.general.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

	private final LeaderboardService leaderboardService;

	@Operation(
			summary = "Get the leaderboard.",
			description = "Returns a pageable entry of the leaderboard.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Leaderboard fetched successfully"),
			@ApiResponse(responseCode = "404", description = "A user referenced on the leaderboard DNE (this is a critical bug and should be a task if it occurs)")
	})
	@Tag(name = "Leaderboard", description = "Leaderboard management API")
	@GetMapping
	public Page<LeaderboardEntry> getLeaderboard(Pageable pageable) {
		return leaderboardService.getLeaderboard(pageable);
	}

	@Operation(
			summary = "Get a user's rank.",
			description = "Returns a user's row on the leaderboard.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Leaderboard row fetched successfully"),
			@ApiResponse(responseCode = "404", description = "The given user was not found")
	})
	@Tag(name = "Leaderboard", description = "Leaderboard management API")
	@GetMapping("/{userId}")
	public LeaderboardEntry getUserEntry(@PathVariable UUID userId) {
		return leaderboardService.getUserEntry(userId);
	}
}
