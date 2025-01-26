package com.ftcs.accountservice.driver.identification.model;

import com.ftcs.accountservice.driver.shared.AddressType;
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
@Table(name = "AddressDriver", schema = "dbo")
public class AddressDriver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AddressDriverId", nullable = false)
    private Integer addressDriverId;

    @Column(name = "Notes", length = 255)
    private String notes;

    @Column(name = "StreetAddress", length = 255, nullable = false)
    private String streetAddress;

    @Column(name = "WardId", nullable = false)
    private Integer wardId;

    @Column(name = "DistrictId", nullable = false)
    private Integer districtId;

    @Column(name = "ProvinceId", nullable = false)
    private Integer provinceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "AddressType", length = 50)
    private AddressType addressType;

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
