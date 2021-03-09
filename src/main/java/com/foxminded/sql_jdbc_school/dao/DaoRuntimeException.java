package com.foxminded.sql_jdbc_school.dao;

public class DaoRuntimeException extends RuntimeException {

    public DaoRuntimeException(String message) {
        super(message);
    }

    public DaoRuntimeException(Throwable cause) {
        super(cause);
    }

    public DaoRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}