package com.ftcs.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralResponseDto<T> {
    @Setter
    public PaginationDto pagination;
    public List<String> header;
    public T content;

    public GeneralResponseDto(T content, Class<?> clazz) {
        this.content = content;
        this.header = this.getFieldNames(clazz);
    }

    /**
     * @param content   the list value of T, have not cut down to pageSize and index
     * @param clazz     the class of the T
     * @param pageIndex the index of the page
     * @param pageSize  the size of the page
     * @param totalRow  the total number of rows
     */
    public GeneralResponseDto(T content, Class<?> clazz, int pageIndex, int pageSize, long totalRow) {
        this.content = content;
        this.header = this.getFieldNames(clazz);
        this.pagination = PaginationDto.of(pageIndex, pageSize, totalRow);
    }

    /**
     * @param content   the list value of T, have not cut down to pageSize and index
     * @param clazz     the class of the T
     * @param pageIndex the index of the page
     * @param pageSize  the size of the page
     * @param totalRow  the total number of rows
     */
    public GeneralResponseDto(T content, Class<?> clazz, int pageIndex, int pageSize, int totalRow) {
        this.content = content;
        this.header = this.getFieldNames(clazz);
        this.pagination = PaginationDto.of(pageIndex, pageSize, totalRow);
    }

    public GeneralResponseDto(T content, Class<?> clazz, PaginationDto pagination) {
        this.content = content;
        this.header = this.getFieldNames(clazz);
        this.pagination = pagination;
    }

    public List<String> getFieldNames(Class<?> clazz) {
        List<String> fieldNames = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    public List<String> insertFieldNames(Class<?> clazz, List<String> fieldNames) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    public void setHeader(Class<?> clazz) {
        this.header = getFieldNames(clazz);
    }

}
