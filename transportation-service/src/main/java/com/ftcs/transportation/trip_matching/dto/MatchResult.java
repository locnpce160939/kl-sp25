package com.ftcs.transportation.trip_matching.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class MatchResult {
    private Integer commonPoints;
    private Integer totalCustomerPoints;
}
