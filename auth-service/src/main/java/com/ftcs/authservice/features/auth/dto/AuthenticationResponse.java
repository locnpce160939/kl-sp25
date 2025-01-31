package com.ftcs.authservice.features.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AuthenticationResponse(
        @JsonProperty("access_token")
        String accessToken,
        String username,
        String fullName,
        Integer userId,
        String role
) {
}
