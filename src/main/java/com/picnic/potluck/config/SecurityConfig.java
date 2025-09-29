package com.picnic.potluck.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.picnic.potluck.service.auth.DbUserDetailsService;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private SecurityScheme createAPIKeyScheme() {
		return new SecurityScheme().type(SecurityScheme.Type.HTTP)
				.bearerFormat("JWT")
				.scheme("bearer");
	}

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI().addSecurityItem(new SecurityRequirement()
						.addList("Bearer Authentication"))
				.components(new Components()
						.addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
				.info(new Info().title("Potluck API")
						.description("The Potluck SpringBoot API.")
						.version("1.0").contact(new Contact().name("Vitaly Vakhteev")
								.email("www.techusage.com").url("vitaly.v@techusage.com"))
						.license(new License().name("GPL-3.0")
								.url("https://www.gnu.org/licenses/gpl.html")));
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	JwtEncoder jwtEncoder(@Value("${app.jwt.secret}") String secret) {
		var key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
		var jwkSource = new ImmutableSecret<>(key);
		return new NimbusJwtEncoder(jwkSource);
	}

	@Bean
	JwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String secret) {
		var key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
		return NimbusJwtDecoder.withSecretKey(key).build();
	}

	@Bean
	JwtAuthenticationConverter jwtAuthConverter() {
		var c = new JwtAuthenticationConverter();
		c.setJwtGrantedAuthoritiesConverter(jwt -> {
			var role = (String) jwt.getClaims().getOrDefault("role", "SEEKER");
			return java.util.List.of(new SimpleGrantedAuthority("ROLE_" + role));
		});
		return c;
	}

	@Bean
	AuthenticationProvider authProvider(DbUserDetailsService uds, PasswordEncoder enc) {
		var provider = new DaoAuthenticationProvider(uds);
		provider.setPasswordEncoder(enc);
		return provider;
	}


	@Bean
	BearerTokenResolver headerThenCookieResolver(@Value("${app.jwt.cookie-name:potluck_token}") String cookieName) {
		return request -> {
			String auth = request.getHeader("Authorization");
			if (auth != null && auth.startsWith("Bearer ")) {
				return auth.substring(7);
			}
			if (request.getCookies() != null) {
				for (var c : request.getCookies()) {
					if (cookieName.equals(c.getName())) {
						return c.getValue();
					}
				}
			}
			return null;
		};
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider provider,
											JwtAuthenticationConverter conv, BearerTokenResolver headerThenCookieResolver) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.authenticationProvider(provider)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/actuator/health", "/swagger-ui/**", "/v3/api-docs/**",
								"/api/auth/login", "/api/auth/logout", "/api/auth/signup"
						).permitAll()
						.requestMatchers(HttpMethod.GET,
								"/api/users/search",
								"/api/users/id/**",
								"/api/users/u/**",
								"/api/leaderboard/**",
								"/api/fundraisers/**"
						).permitAll()
						.requestMatchers(HttpMethod.POST,
								"/api/fundraisers/near"
						).permitAll()
						.anyRequest().authenticated()
				)
				.oauth2Login(oauth -> oauth
						.successHandler((req, res, auth) -> {
							res.sendRedirect("/auth/success");
						})
				)
				.oauth2ResourceServer(oauth2 -> oauth2
						.bearerTokenResolver(headerThenCookieResolver)
						.jwt(jwt -> jwt.jwtAuthenticationConverter(conv)))
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((req, res, e) -> res.sendError(401))
						.accessDeniedHandler((req, res, e) -> res.sendError(403))
				);
		return http.build();
	}
}
