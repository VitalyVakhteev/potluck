package com.picnic.potluck.service.user;

import com.picnic.potluck.dto.user.ProfileRequest;
import com.picnic.potluck.dto.user.ProfileResponse;
import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository users;

    @Transactional
    public ProfileResponse modifyProfile(UUID userId, ProfileRequest req) {
        var u = users.findById(userId).orElseThrow();

        if (req.bio() != null) {
            u.setBio(trimToNull(req.bio()));
        }
        if (req.location() != null) {
            u.setLocation(trimToNull(req.location()));
        }
        if (req.bannerColor() != null) {
            u.setBannerColor(req.bannerColor());
        }
        if (req.displayEmail() != null) {
            u.setDisplayEmail(req.displayEmail());
        }
        if (req.displayPhone() != null) {
            u.setDisplayPhone(req.displayPhone());
        }
        // JPA dirty checking will persist changes at tx commit, so we convert to a DTO
        return toDto(u);
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        var t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static ProfileResponse toDto(User u) {
        return new ProfileResponse(
                u.getId(),
                u.getUsername(),
                u.getBio(),
                u.getLocation(),
                u.getBannerColor(),
                u.isDisplayEmail(),
                u.isDisplayPhone(),
                u.getTotalPoints()
        );
    }
}
