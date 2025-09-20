package com.picnic.potluck.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.picnic.potluck.service.auth.DbUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.crypto.spec.SecretKeySpec;

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
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

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
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider provider,
                                            JwtAuthenticationConverter conv) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Todo: figure out how to allow oidc with csrf disabled
            .cors(Customizer.withDefaults())
            .authenticationProvider(provider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/actuator/health", "/swagger-ui/**", "/v3/api-docs/**",
                        "/api/fundraisers", "/api/fundraisers/**",
                        "/api/users/id/**", "/api/users/u/**",
                        "/api/leaderboard/**",
                        "/api/auth/login", "/api/auth/signup", "/api/auth/oidc/me"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                    .successHandler((req, res, auth) -> {
                        res.sendRedirect("/auth/success");
                    })
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(conv)))
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((req, res, e) -> res.sendError(401))
            );
        return http.build();
    }
}
