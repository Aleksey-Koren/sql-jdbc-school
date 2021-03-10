package com.foxminded.sql_jdbc_school.domain;

public class DomainRuntimeException extends RuntimeException {

    public DomainRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainRuntimeException(String message) {
        super(message);
    }

    public DomainRuntimeException(Throwable cause) {
        super(cause);
    }    
}
