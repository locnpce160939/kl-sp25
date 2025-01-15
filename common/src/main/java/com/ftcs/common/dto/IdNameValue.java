package com.ftcs.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IdNameValue<K, V> {
    private K id;
    private String name;
    private V value;

}
