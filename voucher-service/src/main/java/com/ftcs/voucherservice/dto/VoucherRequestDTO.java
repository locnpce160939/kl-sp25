package com.ftcs.voucherservice.dto;

import com.ftcs.authservice.features.account.contacts.Rank;
import com.ftcs.voucherservice.constant.DiscountType;
import com.ftcs.voucherservice.constant.PaymentMethod;
import com.ftcs.voucherservice.constant.UserType;
import com.ftcs.voucherservice.constant.VoucherStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherRequestDTO {
    private String code;
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
    private Boolean isFirstOrder;
    private VoucherStatus status;
    private Integer usageLimit;
    private PaymentMethod paymentMethod;
    private UserType userType;
    private Integer pointsRequired;
    private Rank minimumRank;
}
