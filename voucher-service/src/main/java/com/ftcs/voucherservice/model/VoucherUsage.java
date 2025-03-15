package com.ftcs.voucherservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "VoucherUsage")
public class VoucherUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VoucherUsage")
    private Long voucherUsageId;

    @Column(name = "AccountId", nullable = false)
    private Integer accountId;

    @Column(name = "VoucherId", nullable = false)
    private Long voucherId;

    @Column(name = "UsageCount", nullable = false)
    private Integer usageCount;

    @Column(name = "LastUsageAt")
    private LocalDateTime lastUsageAt;

    @Column(name = "CreateAt", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "UpdateAt", nullable = false)
    private LocalDateTime updateAt;

    @Column(name = "IsRedeemed", nullable = false)
    private Boolean isRedeemed;

    @Column(name = "RedemptionDate")
    private LocalDateTime redemptionDate;
}
