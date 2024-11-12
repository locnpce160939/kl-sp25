package com.ftcs.authservice.features.auth.dto;

import lombok.Builder;

@Builder
public record AuthenticationRequest(
        String username,
        String password
) {
}
