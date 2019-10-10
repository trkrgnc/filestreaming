package com.garanti.test.customerproject.error;

public class ParameterException extends Exception {
    String reason;

    public ParameterException(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
