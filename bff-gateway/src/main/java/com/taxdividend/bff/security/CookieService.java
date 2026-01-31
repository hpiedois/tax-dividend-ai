package com.taxdividend.bff.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CookieService {

    @Value("${security.cookie.access-token-name:ACCESS_TOKEN}")
    private String accessTokenName;

    @Value("${security.cookie.refresh-token-name:REFRESH_TOKEN}")
    private String refreshTokenName;

    @Value("${security.cookie.access-token-duration:900}")
    private long accessTokenDuration;

    @Value("${security.cookie.refresh-token-duration:604800}")
    private long refreshTokenDuration;

    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from(accessTokenName, token)
                .httpOnly(true)
                .secure(true) // Should depend on profile, but consistent with previous code
                .path("/")
                .maxAge(accessTokenDuration)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from(refreshTokenName, token)
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(refreshTokenDuration)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie clearAccessTokenCookie() {
        return ResponseCookie.from(accessTokenName, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie clearRefreshTokenCookie() {
        return ResponseCookie.from(refreshTokenName, "")
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}
