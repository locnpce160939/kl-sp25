package com.ftcs.insuranceservice.insurance_claim.model;

import com.ftcs.insuranceservice.insurance_claim.constant.ClaimStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "InsuranceClaim")
public class InsuranceClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @JoinColumn(name = "BookingInsuranceId", nullable = false)
    private Long bookingInsuranceId;

    @Column(name = "ClaimDescription", columnDefinition = "TEXT")
    private String claimDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "ClaimStatus", length = 50)
    private ClaimStatus claimStatus;

    @Column(name = "ClaimDate", nullable = false)
    private LocalDateTime claimDate;

    @Column(name = "ResolutionDate")
    private LocalDateTime resolutionDate;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
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
