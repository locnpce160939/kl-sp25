package com.ftcs.voucherservice.dto;

import com.ftcs.voucherservice.constant.DiscountType;
import com.ftcs.voucherservice.constant.PaymentMethod;
import com.ftcs.voucherservice.constant.VoucherStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVoucherRequestDTO {
    private String title;
    private String description;
    private DiscountType discountType;
    private Double discountValue;
    private Double minOrderValue;
    private Double minKm;
    private Double maxDiscountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer quantity;
    private Boolean isFirst;
    private VoucherStatus status;
    private Integer usageLimit;
    private PaymentMethod paymentMethod;
}
