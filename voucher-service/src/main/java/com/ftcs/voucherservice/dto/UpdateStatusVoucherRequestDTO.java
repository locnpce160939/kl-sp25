package com.ftcs.voucherservice.dto;

import com.ftcs.voucherservice.constant.VoucherStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusVoucherRequestDTO {
    private VoucherStatus status;
}
