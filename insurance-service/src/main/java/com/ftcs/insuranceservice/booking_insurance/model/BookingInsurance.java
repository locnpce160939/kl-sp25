package com.ftcs.insuranceservice.booking_insurance.model;

import io.swagger.models.auth.In;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BookingInsurance")
public class BookingInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "BookingId", nullable = false)
    private Long bookingId;

    @Column(name = "PolicyId", nullable = false)
    private Long policyId;

    @Column(name = "AccountId")
    private Integer accountId;

    @Column(name = "CalculatedPremium")
    private Double calculatedPremium;

    @Column(name = "PremiumPercentage")
    private Double premiumPercentage;

    @Column(name = "CompensationPercentage")
    private Double compensationPercentage;

    @Column(name = "CalculateCompensation")
    private Double calculateCompensation;

    @Column(name = "Status", length = 50)
    private String status;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}