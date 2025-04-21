package com.ftcs.bonusservice.dto;

import com.ftcs.authservice.features.account.contacts.Rank;
import com.ftcs.authservice.features.account.contacts.StatusAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Integer accountId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String profilePicture;
    private StatusAccount status;
    private Double balance;
    private String notes;
    private Rank ranking;
    private Integer loyaltyPoints;
    private LocalDateTime lastLogin;
}