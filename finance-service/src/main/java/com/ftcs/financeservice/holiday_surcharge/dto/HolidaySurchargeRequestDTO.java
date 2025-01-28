package com.ftcs.financeservice.holiday_surcharge.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class HolidaySurchargeRequestDTO {
    private String holidayName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double surchargePercentage;
}
