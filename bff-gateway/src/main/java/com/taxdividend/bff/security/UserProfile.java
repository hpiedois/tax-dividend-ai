package com.taxdividend.bff.security;

public record UserProfile(String id, String email, String fullName, String taxId) {
}
