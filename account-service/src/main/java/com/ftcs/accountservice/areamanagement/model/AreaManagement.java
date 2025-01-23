package com.ftcs.accountservice.areamanagement.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "AreaManagement", schema = "dbo")
public class AreaManagement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AreaManagementId", nullable = false)
    private Integer areaManagementId;
    @Column(name = "AccountId", nullable = false)
    private Integer accountId;
    @Column(name = "ProvinceId", nullable = false)
    private Integer provinceId;
}
