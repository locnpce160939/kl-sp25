package com.ftcs.common.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyAndValue<KeyType,ValueType> {
    private KeyType key;
    private ValueType value;
}
