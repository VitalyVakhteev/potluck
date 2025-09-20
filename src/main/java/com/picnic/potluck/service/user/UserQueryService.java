package com.picnic.potluck.service.user;

import com.picnic.potluck.dto.user.UserDetail;
import com.picnic.potluck.dto.user.UserSummary;
import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.user.UserFavoriteFundraiserRepository;
import com.picnic.potluck.repository.user.UserFollowRepository;
import com.picnic.potluck.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserQueryService {
    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final UserFavoriteFundraiserRepository userFavoriteFundraiserRepository;

    static String maskEmail(String email) {
        if (email == null) return null;
        var parts = email.split("@");
        if (parts.length != 2) return null;
        var name = parts[0];
        var domain = parts[1];
        var visible = Math.min(2, name.length());
        return name.substring(0, visible) + "…" + "@" + domain;
    }

    static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return null;
        return "•••" + phone.substring(phone.length() - 4);
    }

    public UserDetail getUserForViewer(UUID targetUserId, @Nullable UUID viewerUserId) {
        var user = userRepository.findById(targetUserId).orElseThrow();
        return buildDetail(user, viewerUserId);
    }

    public UserDetail getUserForViewer(String username, @Nullable UUID viewerUserId) {
        var user = userRepository.findByUsernameIgnoreCase(username).orElseThrow();
        return buildDetail(user, viewerUserId);
    }

    public UserSummary getSummary(UUID userId) {
        var u = userRepository.findById(userId).orElseThrow();
        return new UserSummary(u.getId(), u.getUsername(), u.getTotalPoints(), u.getTotalFundraisers());
    }

    private UserDetail buildDetail(User user, @Nullable UUID viewerUserId) {

        long followers = userFollowRepository.countByUserId(user.getId());
        long following = userFollowRepository.countByFollowerId(user.getId());
        long favorites = userFavoriteFundraiserRepository.countByUserId(user.getId());

        boolean isSelf = viewerUserId != null && viewerUserId.equals(user.getId());

        String emailOut = (user.isDisplayEmail() || isSelf) ? user.getEmail() : maskEmail(user.getEmail());
        String phoneOut = (user.isDisplayPhone() || isSelf) ? user.getPhoneNumber() : maskPhone(user.getPhoneNumber());

        return new UserDetail(
                user.getId(),
                user.getUsername(),
                user.getBio(),
                user.getLocation(),
                user.getBannerColor(),
                user.getTotalPoints(),
                user.getTotalFundraisers(),
                followers, following, favorites,
                emailOut, phoneOut
        );
    }
}
