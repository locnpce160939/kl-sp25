package com.ftcs.insuranceservice.insurance_claim.model;

import com.ftcs.insuranceservice.insurance_claim.constant.ClaimStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "InsuranceClaim", schema = "dbo")
public class InsuranceClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "BookingId", nullable = false)
    private Long bookingId;

    @Column(name = "BookingInsuranceId", nullable = false)
    private Long bookingInsuranceId;

    @Column(name = "ClaimDescription", length = 500)
    private String claimDescription;

    @Column(name = "EvidenceImages")
    private String evidenceImages; // Store as comma-separated string

    @Column(name = "ClaimDate")
    private LocalDateTime claimDate;

    @Column(name = "ResolutionDate")
    private LocalDateTime resolutionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "ClaimStatus")
    private ClaimStatus claimStatus;

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

    public List<String> getEvidenceImageList() {
        if (evidenceImages == null || evidenceImages.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(evidenceImages.split(","));
    }

    public void setEvidenceImageList(List<String> images) {
        if (images == null || images.isEmpty()) {
            this.evidenceImages = null;
        } else {
            this.evidenceImages = String.join(",", images);
        }
    }
}
