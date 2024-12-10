package com.ftcs.accountservice.driver.identification.model;

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
@Table(name = "DriverIdentification", schema = "dbo")
public class DriverIdentification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DriverIdentificationId", nullable = false)
    private Integer driverIdentificationId;

    @Column(name = "AccountId", nullable = false)
    private Integer accountId;

    @Column(name = "IDNumber", length = 50, nullable = false)
    private String idNumber;

    @Column(name = "Status", length = 50)
    private String status;

    @Column(name = "PermanentAddress", length = 255)
    private Integer permanentAddress;

    @Column(name = "TemporaryAddress", length = 255)
    private Integer temporaryAddress;

    @Column(name = "IssueDate")
    private LocalDateTime issueDate;

    @Column(name = "ExpiryDate")
    private LocalDateTime expiryDate;

    @Column(name = "IssuedBy", length = 100)
    private String issuedBy;

    @Column(name = "IsVerified")
    private Boolean isVerified;

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