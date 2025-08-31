package com.parkplus.common.error;

public class ApiException extends RuntimeException {

    private final String code;
    private final int status;

    public ApiException(String code, String msg, int status) {
        super(msg);
        this.code = code;
        this.status = status;
    }

    public String code() {
        return code;
    }

    public int status() {
        return status;
    }
}