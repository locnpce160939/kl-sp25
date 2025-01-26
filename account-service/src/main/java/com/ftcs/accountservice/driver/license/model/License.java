package com.ftcs.accountservice.driver.license.model;

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
@Table(name = "License", schema = "dbo")
public class License {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LicenseId", nullable = false)
    private Integer licenseId;

    @Column(name = "AccountId", nullable = false)
    private Integer accountId;

    @Column(name = "LicenseNumber", length = 50, nullable = false)
    private String licenseNumber;

    @Column(name = "LicenseType", length = 50)
    private String licenseType;

    @Column(name = "IssuedDate")
    private LocalDateTime issuedDate;

    @Column(name = "ExpiryDate")
    private LocalDateTime expiryDate;

    @Column(name = "IssuingAuthority", length = 100)
    private String issuingAuthority;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 50)
    private StatusDocumentType status;

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