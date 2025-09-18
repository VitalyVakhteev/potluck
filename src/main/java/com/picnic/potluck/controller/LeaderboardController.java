package com.picnic.potluck.controller;

import com.picnic.potluck.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public Page<LeaderboardService.LeaderboardEntry> getLeaderboard(Pageable pageable) {
        return leaderboardService.getLeaderboard(pageable);
    }
}
