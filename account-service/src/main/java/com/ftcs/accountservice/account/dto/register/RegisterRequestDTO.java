package com.ftcs.accountservice.account.dto.register;

import com.ftcs.authservice.features.account.contacts.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
//    @NotBlank(message = "Username must not be null or empty")
    private String username;

//    @NotBlank(message = "Password must not be null or empty")
    private String password;

//    @NotBlank(message = "FullName must not be null or empty")
    private String fullName;

//    @NotBlank(message = "Email must not be null or empty")
    @Email(message = "Email is not valid")
    private String email;

//    @NotBlank(message = "Phone must not be null or empty")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone number is not valid")
    private String phone;

    private RoleType role;
}
