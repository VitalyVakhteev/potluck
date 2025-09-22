package com.picnic.potluck.service.security;

import com.picnic.potluck.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {
	private final JwtEncoder encoder;

	@Value("${app.jwt.issuer}")
	private String issuer;

	@Value("${app.jwt.access-minutes}")
	private long accessMinutes;

	public String issue(User user) {
		var now = Instant.now();
		var expires = now.plusSeconds(accessMinutes * 60);

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(issuer)
				.issuedAt(now)
				.expiresAt(expires)
				.subject(user.getId().toString())
				.claim("username", user.getUsername())
				.claim("role", user.getRole().name())
				.build();

		JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();

		return encoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
	}
}
