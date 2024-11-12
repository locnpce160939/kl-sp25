package com.ftcs.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchObjectGenericDto {
    String operation;

    List<Object> searchValues;

    String searchField;


}
