package com.ftcs.accountservice.driver.vehicle.model;

import com.ftcs.accountservice.driver.shared.StatusDocumentType;
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
@Table(name = "Vehicle", schema = "dbo")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VehicleId", nullable = false)
    private Integer vehicleId;

    @Column(name = "AccountId", nullable = false)
    private Integer accountId;

    @Column(name = "LicensePlate", length = 50, nullable = false)
    private String licensePlate;

    @Column(name = "VehicleType", length = 50)
    private String vehicleType;

    @Column(name = "Make", length = 50)
    private String make;

    @Column(name = "Model", length = 50)
    private String model;

    @Column(name = "Year")
    private Integer year;

    @Column(name = "Capacity")
    private Integer capacity;

    @Column(name = "Dimensions", length = 100)
    private String dimensions;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 50)
    private StatusDocumentType status;

    @Column(name = "InsuranceStatus", length = 50)
    private String insuranceStatus;

    @Column(name = "RegistrationExpiryDate")
    private LocalDateTime registrationExpiryDate;

    @Column(name = "Notes", length = 255)
    private String notes;

    @Column(name = "FrontView")
    private String frontView;

    @Column(name = "BackView")
    private String backView;

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