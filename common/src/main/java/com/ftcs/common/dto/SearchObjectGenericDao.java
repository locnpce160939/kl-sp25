package com.ftcs.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchObjectGenericDao {
    @JsonProperty("Operation")
    String operation;

    @JsonProperty("SearchValues")
    List<Object> searchValues;

    @JsonProperty("SearchField")
    String searchField;

    public SearchObjectGenericDao(SearchObjectGenericDto dto) {
        this.operation = dto.getOperation();
        this.searchValues = dto.getSearchValues();
        this.searchField = dto.getSearchField();
    }


}
