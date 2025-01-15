package com.ftcs.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchObject {
    int LevelSearch;
    String Operation;
    String SearchValue;
    String SearchField;

    public SearchObject(SearchCriteria criteria, int levelId) {
        this.setLevelSearch(levelId);
        this.setOperation(criteria.getOperation());
        this.setSearchValue(criteria.getSearchValue());
        this.setSearchField(criteria.getSearchField());
    }
}
