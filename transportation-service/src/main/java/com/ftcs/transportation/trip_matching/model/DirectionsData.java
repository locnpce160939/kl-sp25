package com.ftcs.transportation.trip_matching.model;

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
@Table(name = "DirectionsData", schema = "dbo")
public class DirectionsData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Distance")
    private Integer distance;

    @Column(name = "Duration")
    private Integer duration;

    @Column(name = "StartAddress")
    private String startAddress;

    @Column(name = "EndAddress")
    private String endAddress;

    @Column(name = "EndLocationLat")
    private String endLocationLat;

    @Column(name = "EndLocationLng")
    private String endLocationLng;

    @Column(name = "StartLocationLat")
    private String startLocationLat;

    @Column(name = "StartLocationLng")
    private String startLocationLng;

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
