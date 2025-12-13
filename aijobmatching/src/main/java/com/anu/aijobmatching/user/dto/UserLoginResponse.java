package com.anu.aijobmatching.user.dto;

public record UserLoginResponse(Long id,
        String name,
        String email,
        String token) {
}
