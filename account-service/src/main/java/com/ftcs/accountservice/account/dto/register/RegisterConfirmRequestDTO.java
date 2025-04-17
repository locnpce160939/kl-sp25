package com.ftcs.accountservice.account.dto.register;

import com.ftcs.authservice.features.account.contacts.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterConfirmRequestDTO {

    private String username;

    private String password;

    private String email;

    private String fullName;

    private String phone;

    private RoleType role;

    private String otp;
}
