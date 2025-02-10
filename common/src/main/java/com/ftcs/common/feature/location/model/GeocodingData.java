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
@Table(name = "GeocodingData", schema = "dbo")
public class GeocodingData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Address")
    private String address;

    @Column(name = "FullData")
    private String fullData;

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
