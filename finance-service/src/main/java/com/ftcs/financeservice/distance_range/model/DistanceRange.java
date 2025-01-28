package com.ftcs.financeservice.distance_range.model;

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
@Table(name = "DistanceRange", schema = "dbo")
public class DistanceRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DistanceRangeId", nullable = false)
    private Integer distanceRangeId;

    @Column(name = "MinKm", nullable = false)
    private Double minKm;

    @Column(name = "MaxKm", nullable = false)
    private Double maxKm;

    @CreationTimestamp
    @Column(name = "CreatedDate", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "UpdatedDate")
    private LocalDateTime updatedDate;

    @Column(name = "UpdatedBy")
    private Integer updatedBy;
}
