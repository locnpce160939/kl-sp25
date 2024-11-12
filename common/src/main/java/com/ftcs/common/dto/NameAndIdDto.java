package com.ftcs.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameAndIdDto {
    String name;
    Integer id;

    public NameAndIdDto(Integer id) {
        this.id = id;
    }
}
