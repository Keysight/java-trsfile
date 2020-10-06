package com.riscure.trs;

public class TRSFormatException extends Exception {
    public TRSFormatException(String message) {
        super(message);
    }

    public TRSFormatException(Throwable throwable) {
        super(throwable);
    }

    public TRSFormatException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
