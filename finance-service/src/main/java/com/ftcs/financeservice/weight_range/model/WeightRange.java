package com.ftcs.financeservice.weight_range.model;

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
@Table(name = "WeightRange", schema = "dbo")
public class WeightRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WeightRangeId", nullable = false)
    private Integer weightRangeId;

    @Column(name = "MinWeight", nullable = false)
    private Double minWeight;

    @Column(name = "MaxWeight", nullable = false)
    private Double maxWeight;

    @CreationTimestamp
    @Column(name = "CreatedDate", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "UpdatedDate")
    private LocalDateTime updatedDate;

    @Column(name = "UpdatedBy")
    private Integer updatedBy;
}
