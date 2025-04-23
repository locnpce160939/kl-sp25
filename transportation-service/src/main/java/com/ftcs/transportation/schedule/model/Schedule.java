package com.ftcs.transportation.schedule.model;

import com.ftcs.transportation.schedule.constant.ScheduleStatus;
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
@Table(name = "Schedule", schema = "dbo")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ScheduleId", nullable = false)
    private Long scheduleId;

    @Column(name = "AccountId", nullable = false)
    private Integer accountId;

    @Column(name = "StartLocation", length = 255, nullable = false)
    private String startLocation;

    @Column(name = "EndLocation", length = 255, nullable = false)
    private String endLocation;

    @Column(name = "StartLocationAddress", length = 255)
    private String startLocationAddress;

    @Column(name = "EndLocationAddress", length = 255)
    private String endLocationAddress;

    @Column(name = "StartDate", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "Vehicle", nullable = false)
    private Integer vehicleId;

    @Column(name = "LocationDriver")
    private String locationDriver;

    @Column(name = "AvailableCapacity", nullable = false)
    private Integer availableCapacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 50, nullable = false)
    private ScheduleStatus status;

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