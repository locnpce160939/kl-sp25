package com.ftcs.authservice.features.account.dto;


import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.contacts.RoleType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class AccountDto {
    private Integer id;
    private String username;
    private RoleType role;
    private String profilePicture;

    public static AccountDto mapToAccountDto(Account account) {
        return new AccountDto(account.getAccountId(), account.getUsername(), account.getRole(), account.getProfilePicture());
    }
}

