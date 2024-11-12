package com.ftcs.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
    @JsonProperty
    String SearchField;
    @JsonProperty
    String Operation;
    @JsonProperty
    String SearchValue;
}
