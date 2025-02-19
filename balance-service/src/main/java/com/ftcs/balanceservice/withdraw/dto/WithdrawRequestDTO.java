package com.ftcs.balanceservice.withdraw.dto;

import com.ftcs.balanceservice.withdraw.constant.BankName;
import com.ftcs.balanceservice.withdraw.constant.WithdrawStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequestDTO {
    private Double amount;
    private WithdrawStatus status;
    private BankName bankName;
    private String bankAccountNumber;
}
