package com.ftcs.balanceservice.withdraw.dto;

import com.ftcs.balanceservice.withdraw.constant.WithdrawStatus;
import lombok.Data;

import java.util.List;
@Data
public class BatchUpdateRequestDTO {
    private List<Long> withdrawIds;
    private WithdrawStatus status;
}
