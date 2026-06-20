package com.nec.middleware.rbacAuth.auth.exception;

import lombok.Getter;

@Getter
public class AuthAspectException extends RuntimeException {
    private final String errTraceMethod;

    public AuthAspectException(String errTraceMethod, String message) {
        super(message);
        this.errTraceMethod = errTraceMethod;
    }
}
