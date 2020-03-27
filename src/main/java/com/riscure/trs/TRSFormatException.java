package com.riscure.trs;

public class TRSFormatException extends Exception {
    public TRSFormatException(String message) {
        super(message);
    }

    public TRSFormatException(String formattedMessage, Object... values) {
        super(String.format(formattedMessage, values));
    }
}
