package com.ftcs.accountservice.account.dto;

import com.ftcs.authservice.features.account.contacts.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequestDTO {
    private RoleType role;
}
