package com.ftcs.financeservice.pricing.model;

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
@Table(name = "Pricing", schema = "dbo")
public class Pricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PricingId", nullable = false)
    private Integer pricingId;

    @Column(name = "DistanceRangeId", nullable = false)
    private Integer distanceRangeId;

    @Column(name = "WeightRangeId", nullable = false)
    private Integer weightRangeId;

    @Column(name = "BasePrice", nullable = false)
    private Double basePrice;

    @CreationTimestamp
    @Column(name = "CreatedDate", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "UpdatedDate")
    private LocalDateTime updatedDate;

    @Column(name = "UpdatedBy")
    private Integer updatedBy;
}

