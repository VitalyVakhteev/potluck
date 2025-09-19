package com.picnic.potluck.controller;

import com.picnic.potluck.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public Page<LeaderboardService.LeaderboardEntry> getLeaderboard(Pageable pageable) {
        return leaderboardService.getLeaderboard(pageable);
    }

    @GetMapping("/{userId}")
    public LeaderboardService.LeaderboardEntry getUserEntry(@PathVariable UUID userId) {
        return leaderboardService.getUserEntry(userId);
    }

}
