package com.taxdividend.bff.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.taxdividend.bff.security.TokenService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final TokenService tokenService;
    private final ReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder; // Injected from UserDetails config if available, or we use manual
                                                   // check for now

    @Value("${security.cookie.access-token-name:ACCESS_TOKEN}")
    private String accessTokenName;

    @Value("${security.cookie.refresh-token-name:REFRESH_TOKEN}")
    private String refreshTokenName;

    @Value("${security.cookie.access-token-duration:900}")
    private long accessTokenDuration;

    @Value("${security.cookie.refresh-token-duration:604800}")
    private long refreshTokenDuration;

    private final com.taxdividend.bff.client.api.AuthApi authApi;

    public AuthController(TokenService tokenService, ReactiveUserDetailsService userDetailsService,
            com.taxdividend.bff.client.api.AuthApi authApi) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
        this.authApi = authApi;
        this.passwordEncoder = org.springframework.security.crypto.factory.PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<UserProfile>> login(@RequestBody LoginRequest request, ServerWebExchange exchange) {
        // Authenticate user
        return userDetailsService.findByUsername(request.username())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                .map(u -> {
                    // Determine Authentication object (simplified for now)
                    Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            u, null, u.getAuthorities());

                    String accessToken = tokenService.generateAccessToken(auth);
                    String refreshToken = tokenService.generateRefreshToken(auth);

                    ResponseCookie accessCookie = ResponseCookie.from(accessTokenName, accessToken)
                            .httpOnly(true)
                            .secure(true) // Should be false for localhost dev if not using https? usually secure=true
                                          // works on localhost in modern browsers, but safer to check profile
                            .path("/")
                            .maxAge(accessTokenDuration)
                            .sameSite("Strict")
                            .build();

                    ResponseCookie refreshCookie = ResponseCookie.from(refreshTokenName, refreshToken)
                            .httpOnly(true)
                            .secure(true)
                            .path("/auth/refresh")
                            .maxAge(refreshTokenDuration)
                            .sameSite("Strict")
                            .build();

                    exchange.getResponse().addCookie(accessCookie);
                    exchange.getResponse().addCookie(refreshCookie);

                    return ResponseEntity.ok(new UserProfile("1", u.getUsername(), "John Doe", "1234567890123"));
                })
                .defaultIfEmpty(ResponseEntity.status(401).build());
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(ServerWebExchange exchange) {
        ResponseCookie accessCookie = ResponseCookie.from(accessTokenName, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // Expire immediately
                .sameSite("Strict")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(refreshTokenName, "")
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        exchange.getResponse().addCookie(accessCookie);
        exchange.getResponse().addCookie(refreshCookie);

        return Mono.just(ResponseEntity.ok().build());
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<Void>> register(@RequestBody RegisterRequest request) {
        com.taxdividend.bff.client.model.RegisterUserRequest backendRequest = new com.taxdividend.bff.client.model.RegisterUserRequest();
        backendRequest.setEmail(request.email());
        backendRequest.setPassword(request.password());
        backendRequest.setFullName(request.fullName());

        return authApi.registerUser(backendRequest)
                .map(r -> ResponseEntity.ok().<Void>build());
    }

    @PostMapping("/verify")
    public Mono<ResponseEntity<Void>> verify(String token) {
        return authApi.verifyEmail(token)
                .filter(r -> Boolean.TRUE.equals(r.getVerified()))
                .map(r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    // TODO: Refresh endpoint requires decoding the refresh token from cookie
}

record RegisterRequest(String email, String password, String fullName) {
}

record LoginRequest(String username, String password) {
}

record UserProfile(String id, String email, String fullName, String taxId) {
}
