package com.ftcs.accountservice.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordAccountRequestDTO {
    private String email;
    private String otp;
    private String newPassword;
    private String confirmPassword;
}
