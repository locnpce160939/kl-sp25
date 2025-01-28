package com.ftcs.financeservice.weight_range.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeightRangeRequestDTO {
    private Double minWeight;
    private Double maxWeight;
}
