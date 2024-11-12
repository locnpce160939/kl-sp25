package com.ftcs.common.exception;


import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class FormValidException extends RuntimeException {
    Map<String, String> caughtErr;

    public FormValidException(String msg) {
        super(msg);
    }

    public FormValidException(String msg, Object errors) {
        super(msg);
        if (errors instanceof Map<?, ?> err) {
            this.caughtErr = (Map<String, String>) err;
        } else if (errors instanceof ArrayList<?> err) {
            this.caughtErr = new HashMap<>();
            err.forEach(e -> this.caughtErr.put("error", String.valueOf(e)));
        } else if (errors instanceof BindingResult bindResult) {
            this.caughtErr = bindResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        } else {
            throw new InternalServerException("Unknown error type");
        }

    }

}
