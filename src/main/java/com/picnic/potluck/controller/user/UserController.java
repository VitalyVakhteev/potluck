package com.picnic.potluck.controller.user;

import com.picnic.potluck.dto.user.ProfileRequest;
import com.picnic.potluck.dto.user.ProfileResponse;
import com.picnic.potluck.dto.user.UserDetail;
import com.picnic.potluck.dto.user.UserSummary;
import com.picnic.potluck.service.user.ProfileService;
import com.picnic.potluck.service.user.UserQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserQueryService userQueryService;
    private final ProfileService profileService;

    @GetMapping("/id/{id}")
    public UserDetail getUser(@PathVariable UUID id,
                              @AuthenticationPrincipal Jwt jwt) {
        UUID viewer = (jwt == null) ? null : UUID.fromString(jwt.getSubject());
        return userQueryService.getUserForViewer(id, viewer);
    }

    @GetMapping("/u/{username}")
    public UserDetail getUser(@PathVariable String username,
                              @AuthenticationPrincipal Jwt jwt) {
        UUID viewer = (jwt == null) ? null : UUID.fromString(jwt.getSubject());
        return userQueryService.getUserForViewer(username, viewer);
    }

    @GetMapping("/me")
    public UserDetail me(@AuthenticationPrincipal Jwt jwt) {
        UUID me = UUID.fromString(jwt.getSubject());
        return userQueryService.getUserForViewer(me, me);
    }

    @PatchMapping("/me")
    public ProfileResponse modifyProfile(@AuthenticationPrincipal Jwt jwt,
                                         @RequestBody @Valid ProfileRequest req) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return profileService.modifyProfile(userId, req);
    }

    @GetMapping("/id/{id}/summary")
    public UserSummary summary(@PathVariable UUID id) {
        return userQueryService.getSummary(id);
    }
}
