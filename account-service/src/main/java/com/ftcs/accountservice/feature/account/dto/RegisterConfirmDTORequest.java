package com.ftcs.accountservice.feature.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterConfirmDTORequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String otp;
}
