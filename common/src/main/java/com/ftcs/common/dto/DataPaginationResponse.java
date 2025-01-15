package com.ftcs.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class DataPaginationResponse<T> {
    // Setters
    // Getters
    private int totalPages;
    private int totalElements;
    private int size;
    private List<T> content;

    public DataPaginationResponse(int totalPages, int totalElements, int size, List<T> content) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.size = size;
        this.content = content;
    }

}