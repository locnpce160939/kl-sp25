package com.ftcs.common.dto;

public record PaginationDto(
        int pageIndex,
        int totalPage,
        long totalRow
) {
    public static PaginationDto of(int pageIndex, int pageSize, int totalRow) {
        return new PaginationDto(
                pageIndex,
                pageIndex != -1 ? (int) Math.ceil((double) totalRow / pageSize) : -1,
                Long.valueOf(totalRow)
        );
    }

    public static PaginationDto of(int pageIndex, int pageSize, long totalRow) {
        return new PaginationDto(
                pageIndex,
                pageIndex != -1 ? (int) Math.ceil((double) totalRow / pageSize) : -1,
                totalRow
        );
    }
}
