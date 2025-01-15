package com.ftcs.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchCriteriaDto {
    List<SearchCriteria> filter;
}
