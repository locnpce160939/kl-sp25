package com.ftcs.transportation.trip_agreement.model;

import com.ftcs.transportation.trip_agreement.constant.AgreementStatusType;
import com.ftcs.transportation.trip_matching.constant.PaymentStatusType;
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
    private Long id;

    @Column(name = "TripMatchingId", nullable = false)
    private Long tripMatchingId;

    @Column(name = "ScheduleId", nullable = false)
    private Long scheduleId;

    @Column(name = "BookingId", nullable = false)
    private Long bookingId;

    @Column(name = "DriverId", nullable = false)
    private Integer driverId;

    @Column(name = "CustomerId", nullable = false)
    private Integer customerId;

    @Column(name = "TotalPrice", nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "PaymentStatus", length = 50, nullable = false)
    private PaymentStatusType paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "AgreementStatus", length = 50, nullable = false)
    private AgreementStatusType agreementStatus;

    @Column(name = "Distance", nullable = false)
    private Integer distance;

    @Column(name = "EstimatedDuration", nullable = false)
    private Integer estimatedDuration;

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
