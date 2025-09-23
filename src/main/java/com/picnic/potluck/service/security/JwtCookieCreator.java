package com.picnic.potluck.service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieCreator {

	@Value("${app.jwt.cookie-name:potluck_token}")
	private String cookieName;

	@Value("${app.jwt.cookie-samesite:Lax}")
	private String sameSite;

	@Value("${app.jwt.cookie-secure:false}")
	private boolean secure;

	@Value("${app.jwt.cookie-max-age-seconds:0}")
	private long maxAge;

	public ResponseCookie build(String token) {
		var builder = ResponseCookie.from(cookieName, token)
				.httpOnly(true)
				.secure(secure)
				.sameSite(sameSite)
				.path("/");

		if (maxAge > 0) builder.maxAge(maxAge);
		return builder.build();
	}

	public ResponseCookie clear() {
		return ResponseCookie.from(cookieName, "")
				.httpOnly(true)
				.secure(secure)
				.sameSite(sameSite)
				.path("/")
				.maxAge(0)
				.build();
	}
}
