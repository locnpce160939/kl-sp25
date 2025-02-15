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
@Table(name = "TripMatchingCache", schema = "dbo")
public class TripMatchingCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "ScheduleId")
    private Long scheduleId;

    @Column(name = "BookingId")
    private Long bookingId;

    @Column(name = "DriverStartLocation")
    private String driverStartLocation;

    @Column(name = "DriverEndLocation")
    private String driverEndLocation;

    @Column(name = "CustomerStartLocation")
    private String customerStartLocation;

    @Column(name = "CustomerEndLocation")
    private String customerEndLocation;

    @Column(name = "DriverStartLocationAddress")
    private String driverStartLocationAddress;

    @Column(name = "DriverEndLocationAddress")
    private String driverEndLocationAddress;

    @Column(name = "CustomerStartLocationAddress")
    private String customerStartLocationAddress;

    @Column(name = "CustomerEndLocationAddress")
    private String customerEndLocationAddress;

    @Column(name = "StartTime")
    private LocalDateTime startTime;

    @Column(name = "EndTime")
    private LocalDateTime endTime;

    @Column(name = "CommonPoints")
    private Integer commonPoints;

    @Column(name = "TotalCustomerPoints")
    private Integer totalCustomerPoints;

    @Column(name = "Capacity")
    private Integer capacity;

    @Column(name = "SameDirection")
    private Boolean sameDirection;

    @Column(name = "Status")
    private String status;

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
