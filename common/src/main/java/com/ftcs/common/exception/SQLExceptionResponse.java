package com.ftcs.common.exception;

import java.sql.SQLException;

public class SQLExceptionResponse extends SQLException {

    public SQLExceptionResponse(String message) {
        super(message);
    }
}
