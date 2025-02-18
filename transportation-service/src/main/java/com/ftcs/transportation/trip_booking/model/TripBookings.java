package com.ftcs.transportation.trip_booking.model;

import com.ftcs.transportation.trip_booking.constant.PaymentMethod;
import com.ftcs.transportation.trip_booking.constant.TripBookingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TripBookings", schema = "dbo")
public class TripBookings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookingId", nullable = false)
    private Long bookingId;

    @Column(name = "AccountId", nullable = false)
    private Integer accountId;

    @Column(name = "TripAgreementId", nullable = false)
    private Long tripAgreementId;

    @Column(name = "BookingType", length = 50, nullable = false)
    private String bookingType;

    @Column(name = "BookingDate", nullable = false)
    private LocalDateTime bookingDate;

    @Column(name = "PickupLocation", length = 255, nullable = false)
    private String pickupLocation;

    @Column(name = "DropoffLocation", length = 255, nullable = false)
    private String dropoffLocation;

    @Column(name = "StartLocationAddress", length = 255)
    private String startLocationAddress;

    @Column(name = "EndLocationAddress", length = 255)
    private String endLocationAddress;

    @Column(name = "Capacity", nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 50, nullable = false)
    private TripBookingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "PaymentMethod", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "ExpirationDate")
    private LocalDateTime expirationDate;

    @Column(name = "TotalDistance", nullable = false)
    private Double totalDistance;

    @Column(name = "Price", nullable = false)
    private Double price;

    @Column(name = "Notes", length = 255)
    private String notes;

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