package com.garanti.test.customerproject.error;

public class FormatException extends Exception{
    String reason;

    public FormatException(String reason) {

        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
