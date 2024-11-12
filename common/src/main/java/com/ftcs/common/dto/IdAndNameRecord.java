package com.ftcs.common.dto;

public record IdAndNameRecord<IdType>(
        IdType id,
        String name
) {


}
