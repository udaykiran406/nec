package com.nec.middleware.rbacAuth.auth.exception;

import lombok.Getter;

@Getter
public class SomethingWentWrongException extends RuntimeException {
    private final String errTraceMethod;

    public SomethingWentWrongException(String errTraceMethod, String message) {
        super(message);
        this.errTraceMethod = errTraceMethod;
    }
}
