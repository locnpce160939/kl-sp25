package com.ftcs.accountservice.driver.management.model;

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
@Table(name = "Driver", schema = "dbo")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DriverId", nullable = false)
    private Integer driverId;

    @Column(name = "AccountId", nullable = false)
    private Integer accountId;

    @Column(name = "Status", length = 50) // Assuming "Status" might need a max length.
    private String status;

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
