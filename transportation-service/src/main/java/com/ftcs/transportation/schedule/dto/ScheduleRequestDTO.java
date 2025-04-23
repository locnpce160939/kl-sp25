package com.ftcs.transportation.schedule.dto;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDTO {
    @NotBlank(message = "Start location cannot be blank")
    private String startLocation;

    @NotBlank(message = "End location cannot be blank")
    private String endLocation;

    @NotBlank(message = "Start location cannot be blank")
    private String startLocationAddress;

    @NotBlank(message = "End location cannot be blank")
    private String endLocationAddress;

    private Integer vehicleId;

    @NotNull(message = "Start date cannot be null")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @NotNull(message = "Available capacity cannot be null")
    @Min(value = 500, message = "Available capacity must be at least 500")
    private Integer availableCapacity;
}