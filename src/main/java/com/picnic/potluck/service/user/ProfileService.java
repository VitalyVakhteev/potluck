package com.picnic.potluck.service.user;

import com.picnic.potluck.dto.user.ProfileRequest;
import com.picnic.potluck.dto.user.ProfileResponse;
import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProfileService {
	private final UserRepository users;
	private static final Pattern HEX = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

	@Transactional
	public ProfileResponse modifyProfile(UUID userId, ProfileRequest req) {
		var u = users.findById(userId).orElseThrow();

		applyNameUpdates(u, req);
		if (req.bannerColor() != null) {
			var t = trimToNull(req.bannerColor());
			if (t == null) {
				u.setBannerColor(null);
			} else if (HEX.matcher(t).matches()) {
				u.setBannerColor(t);
			} else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid banner color");
			}
		}
		if (req.bio() != null) {
			u.setBio(trimToNull(req.bio()));
		}
		if (req.location() != null) {
			u.setLocation(trimToNull(req.location()));
		}
		if (req.displayName() != null) {
			u.setDisplayName(req.displayName());
		}
		if (req.displayEmail() != null) {
			u.setDisplayEmail(req.displayEmail());
		}
		if (req.displayPhone() != null) {
			u.setDisplayPhone(req.displayPhone());
		}
		return toDto(u);
	}

	private void applyNameUpdates(User u, ProfileRequest req) {
		boolean willHideName = (req.displayName() != null) ? !req.displayName() : !u.isDisplayName();
		if (willHideName) return;

		if (req.firstName() != null) {
			String t = trimToNull(req.firstName());
			if (t == null) {
				u.setFirstName(null);
			} else if (!t.matches("\\.*")) {
				u.setFirstName(t);
			}
		}

		if (req.lastName() != null) {
			String t = trimToNull(req.lastName());
			if (t == null) {
				u.setLastName(null);
			} else if (!t.matches("\\.*")) {
				u.setLastName(t);
			}
		}
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
				u.getFirstName(),
				u.getLastName(),
				u.getBio(),
				u.getLocation(),
				u.getBannerColor(),
				u.isDisplayName(),
				u.isDisplayEmail(),
				u.isDisplayPhone(),
				u.getTotalPoints()
		);
	}
}
