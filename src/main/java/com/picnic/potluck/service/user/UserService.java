package com.picnic.potluck.service.user;

import com.picnic.potluck.dto.auth.AuthResponse;
import com.picnic.potluck.dto.auth.LoginRequest;
import com.picnic.potluck.dto.auth.SignupRequest;
import com.picnic.potluck.dto.user.ChangePasswordRequest;
import com.picnic.potluck.exception.ApiException;
import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.user.UserRepository;
import com.picnic.potluck.service.security.JwtService;
import com.picnic.potluck.util.PhoneNormalizer;
import com.picnic.potluck.util.Role;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwt;

	@Transactional
	public AuthResponse signup(SignupRequest req) {
		if (req.role() == Role.ADMIN) {
			throw new ApiException("ROLE_NOT_ALLOWED", "Signup does not support ADMIN roles", HttpStatus.BAD_REQUEST);
		}

		userRepository.findByUsernameIgnoreCase(req.username()).ifPresent(u -> {
			throw new ApiException("USERNAME_TAKEN", "Username in use", HttpStatus.CONFLICT);
		});
		userRepository.findByEmailIgnoreCase(req.email()).ifPresent(u -> {
			throw new ApiException("EMAIL_TAKEN", "Email in use", HttpStatus.CONFLICT);
		});
		final String e164 = PhoneNormalizer.toE164(req.phone(), "+1");
		userRepository.findByPhoneNumberIgnoreCase(e164).ifPresent(u -> {
			throw new ApiException("PHONE_TAKEN", "Phone number in use", HttpStatus.CONFLICT);
		});

		var user = User.builder()
				.username(req.username())
				.email(req.email())
				.phoneNumber(e164)
				.passwordHash(passwordEncoder.encode(req.password()))
				.role(req.role())
				.active(true)
				.build();

		try {
			userRepository.save(user);
		} catch (org.springframework.dao.DataIntegrityViolationException ex) {
			String code = "CONFLICT";
			var root = ex.getCause() instanceof ConstraintViolationException ? (ConstraintViolationException) ex.getCause() : null;
			if (root != null) {
				String cn = root.getConstraintName();
				if ("uc_users_username".equalsIgnoreCase(cn)) code = "USERNAME_TAKEN";
				else if ("uc_users_email".equalsIgnoreCase(cn)) code = "EMAIL_TAKEN";
				else if ("uc_users_phone".equalsIgnoreCase(cn)) code = "PHONE_TAKEN";
			}
			throw new ApiException(code, "Duplicate value", HttpStatus.CONFLICT);
		}

		var token = jwt.issue(user);
		return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest req) {
		var auth = new UsernamePasswordAuthenticationToken(req.username(), req.password());
		authenticationManager.authenticate(auth);

		var user = userRepository.findByUsernameIgnoreCase(req.username()).orElseThrow();

		var token = jwt.issue(user);
		return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
	}

	@Transactional
	public void changePassword(UUID userId, ChangePasswordRequest req) {
		var u = userRepository.findById(userId).orElseThrow();

		if (u.getPasswordHash() == null || !passwordEncoder.matches(req.currentPassword(), u.getPasswordHash())) {
			throw new IllegalArgumentException("CURRENT_PASSWORD_INCORRECT");
		}

		if (passwordEncoder.matches(req.newPassword(), u.getPasswordHash())) {
			throw new IllegalArgumentException("PASSWORD_UNCHANGED");
		}

		if (req.newPassword().equalsIgnoreCase(u.getUsername()) || req.newPassword().equalsIgnoreCase(u.getEmail())) {
			throw new IllegalArgumentException("PASSWORD_TOO_SIMILAR");
		}

		u.setPasswordHash(passwordEncoder.encode(req.newPassword()));
	}
}
