package com.ftcs.rateandreview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainStatisticsDTO {
    private String category;
    private String metric;
    private Long value;
} 