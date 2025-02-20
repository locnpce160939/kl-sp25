package com.ftcs.balanceservice.withdraw.dto;

import com.ftcs.balanceservice.withdraw.constant.BankName;
import com.ftcs.balanceservice.withdraw.constant.WithdrawStatus;
import lombok.Data;

@Data
public class WithdrawExportDTO {
    private Long withdrawId;
    private Integer accountId;
    private String username;
    private Double amount;
    private BankName bankName;
    private String bankAccountNumber;
    private WithdrawStatus status;
    private String requestDate;
    private String processedDate;
}
