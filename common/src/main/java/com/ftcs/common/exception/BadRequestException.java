package com.ftcs.common.exception;

import com.ftcs.common.dto.KeyAndValue;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class BadRequestException extends RuntimeException {

    Map<String, String> errorFieldMap = null;
    List<KeyAndValue<String, String>> errorFieldList = null;

    public BadRequestException(String message, Object errorField) {
        super(message);
        if (errorField instanceof Map) {
            this.errorFieldMap = (Map<String, String>) errorField;
        }else if (errorField instanceof List) {
            this.errorFieldList = (List<KeyAndValue<String, String>>) errorField;
        }
    }

    public BadRequestException(String message) {
        super(message);
    }


}
