package com.ftcs.accountservice.areamanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.HttpUrl;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    private Integer accountId;
    private String username;
    private String email;
    private String phone;
    private String role;
    private String profilePicture;
    private LocalDateTime lastLogin;
    private String accountStatus;
}
