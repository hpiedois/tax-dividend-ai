package com.taxdividend.bff.security;

import java.util.List;

public record UserContext(String userId, String email, List<String> roles) {
}
