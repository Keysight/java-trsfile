package com.riscure.trs.enums;

public enum Encoding {
    ILLEGAL (0x00, -1),
    BYTE (0x01, 1),
    SHORT (0x02, 2),
    INT (0x04, 4),
    FLOAT (0x14, 4);

    private final int value;
    private final int size;

    Encoding(int value, int size) {
        this.value = value;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public int getValue() {
        return value;
    }

    public static Encoding fromValue(int value) {
        for (Encoding encoding : Encoding.values()) {
            if (encoding.getValue() == value) return encoding;
        }
        throw new IllegalArgumentException("Unknown Trace encoding: " + value);
    }
}
