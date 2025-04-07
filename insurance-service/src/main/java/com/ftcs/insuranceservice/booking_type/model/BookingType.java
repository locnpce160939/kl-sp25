package com.ftcs.insuranceservice.booking_type.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BookingType", schema = "dbo")
public class BookingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookingTypeId", nullable = false)
    private Long bookingTypeId;

    @Column(name = "BookingTypeName", nullable = false)
    private String bookingTypeName;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
}
