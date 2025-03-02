package com.ftcs.voucherservice.dto;

import com.ftcs.voucherservice.constant.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherValidationDTO {
    private Double orderValue;
    private String paymentMethod;
    private Double distanceKm;
    private Integer accountId;
    private Boolean isFirstOrder;
}
