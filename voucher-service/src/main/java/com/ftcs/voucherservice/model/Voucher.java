package com.ftcs.voucherservice.model;

import com.ftcs.voucherservice.constant.DiscountType;
import com.ftcs.voucherservice.constant.PaymentMethod;
import com.ftcs.voucherservice.constant.VoucherStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Voucher")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VoucherId")
    private Long voucherId;

    @Column(name = "Code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Description")
    private String description;

    @Column(name = "DiscountType", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(name = "DiscountValue", nullable = false)
    private Double discountValue;

    @Column(name = "MinOrderValue")
    private Double minOrderValue;

    @Column(name = "MinKm")
    private Double minKm;

    @Column(name = "MaxDiscountAmount")
    private Double maxDiscountAmount;

    @Column(name = "StartDate", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "EndDate", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "IsFirstOrder")
    private Boolean isFirstOrder;

    @Column(name = "PaymentMethod")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "Status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VoucherStatus status;

    @Column(name = "UsageLimit")
    private Integer usageLimit;

    @CreationTimestamp
    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}
