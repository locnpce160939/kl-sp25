package com.ftcs.accountservice.customer.address.model;

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
@Table(name = "Address", schema = "dbo")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AddressId", nullable = false)
    private Integer addressId;

    @Column(name = "AccountId", nullable = false)
    private Integer accountId;

    @Column(name = "Status", length = 50)
    private String status;

    @Column(name = "Notes", length = 255)
    private String notes;

    @Column(name = "StreetAddress", length = 255)
    private String streetAddress;

    @Column(name = "WardId")
    private Integer wardId;

    @Column(name = "DistrictId")
    private Integer districtId;

    @Column(name = "ProvinceId")
    private Integer provinceId;

    @Column(name = "AddressType", length = 50)
    private String addressType;

    @Column(name = "IsDefault")
    private Boolean isDefault;

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
