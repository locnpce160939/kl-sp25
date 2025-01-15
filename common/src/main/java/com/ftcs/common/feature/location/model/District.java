package com.ftcs.common.feature.location.model;

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
@Table(name = "Districts", schema = "location")
public class District {

    @Id
    @Column(name = "Code", nullable = false, length = 20)
    private Integer code;

    @Column(name = "Name", nullable = false, length = 255)
    private String name;

    @Column(name = "NameENG", length = 255)
    private String nameENG;

    @Column(name = "FullName", length = 255)
    private String fullName;

    @Column(name = "FullNameENG", length = 255)
    private String fullNameENG;

    @Column(name = "CodeName", length = 255)
    private String codeName;

    @Column(name = "ProvinceCode", length = 20)
    private Integer provinceCode;

    @Column(name = "AdministrativeUnitId")
    private Integer administrativeUnitId;

}
