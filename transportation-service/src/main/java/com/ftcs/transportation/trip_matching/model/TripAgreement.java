package com.ftcs.transportation.trip_matching.model;

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
@Table(name = "TripAgreement", schema = "dbo")
public class TripAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Column(name = "TripMatchingId", nullable = false)
    private Integer tripMatchingId;

    @Column(name = "ScheduleId", nullable = false)
    private Integer scheduleId;

    @Column(name = "BookingId", nullable = false)
    private Integer bookingId;

    @Column(name = "DriverId", nullable = false)
    private Integer driverId;

    @Column(name = "CustomerId", nullable = false)
    private Integer customerId;

    @Column(name = "TotalPrice", nullable = false)
    private Double totalPrice;

    @Column(name = "PaymentStatus", length = 50, nullable = false)
    private String paymentStatus;  // "Pending", "Paid", "Failed"

    @Column(name = "AgreementStatus", length = 50, nullable = false)
    private String agreementStatus; // "Pending", "Confirmed", "Cancelled"

    @Column(name = "TripStartTime")
    private LocalDateTime tripStartTime;

    @Column(name = "TripEndTime")
    private LocalDateTime tripEndTime;

    @Column(name = "Distance", nullable = false)
    private Integer distance;

    @Column(name = "EstimatedDuration", nullable = false)
    private Integer estimatedDuration; // minutes

    @CreationTimestamp
    @Column(name = "CreateAt", updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "UpdateAt")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}
