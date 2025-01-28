package com.ftcs.financeservice.holiday_surcharge.model;

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
@Table(name = "HolidaySurcharge", schema = "dbo")
public class HolidaySurcharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HolidaySurchargeId", nullable = false)
    private Integer holidaySurchargeId;

    @Column(name = "HolidayName", nullable = false, length = 255)
    private String holidayName;

    @Column(name = "StartDate", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "EndDate", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "SurchargePercentage", nullable = false)
    private Double surchargePercentage;

    @CreationTimestamp
    @Column(name = "CreatedDate", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "UpdatedDate")
    private LocalDateTime updatedDate;

    @Column(name = "UpdatedBy")
    private Integer updatedBy;
}
