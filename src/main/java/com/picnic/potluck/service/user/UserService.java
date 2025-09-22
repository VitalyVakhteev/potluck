package com.picnic.potluck.service.user;

import com.picnic.potluck.dto.auth.AuthResponse;
import com.picnic.potluck.dto.auth.LoginRequest;
import com.picnic.potluck.dto.auth.SignupRequest;
import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.user.UserRepository;
import com.picnic.potluck.service.security.JwtService;
import com.picnic.potluck.util.PhoneNormalizer;
import com.picnic.potluck.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwt;

	@Transactional
	public AuthResponse signup(SignupRequest req) {
		userRepository.findByEmailIgnoreCase(req.email()).ifPresent(u -> {
			throw new IllegalArgumentException("Email in use");
		});
		userRepository.findByUsernameIgnoreCase(req.username()).ifPresent(u -> {
			throw new IllegalArgumentException("Username in use");
		});
		userRepository.findByPhoneNumberIgnoreCase(req.phone()).ifPresent(u -> {
			throw new IllegalArgumentException("Phone number in use");
		});
		if (req.role() == Role.ADMIN) throw new IllegalArgumentException("Signup does not support ADMIN roles");
		String e164 = PhoneNormalizer.toE164(req.phone(), "+1");
		// Todo: validate email?

		var user = User.builder()
				.username(req.username())
				.email(req.email())
				.phoneNumber(e164)
				.passwordHash(passwordEncoder.encode(req.password()))
				.role(req.role())
				.active(true)
				.build();

		userRepository.save(user);
		var token = jwt.issue(user);
		return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest req) {
		var auth = new UsernamePasswordAuthenticationToken(req.username(), req.password());
		authenticationManager.authenticate(auth);

		var user = userRepository.findByUsernameIgnoreCase(req.username())
				.orElseThrow();

		var token = jwt.issue(user);
		return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
	}
}
