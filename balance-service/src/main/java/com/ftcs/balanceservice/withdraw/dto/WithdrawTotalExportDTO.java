package com.ftcs.balanceservice.withdraw.dto;

import lombok.Data;

@Data
public class WithdrawTotalExportDTO {
    private Integer accountId;
    private String username;
    private Double currentBalance;
    private Double totalWithdrawAmount;
    private Integer totalWithdrawals;
    private Integer totalApproved;
    private Integer totalRejected;
    private Integer totalPending;
    private String exportDate;
}
