package com.riscure.trs.parameter;

public enum ParameterType {
    BYTE (0x01, Byte.BYTES),
    SHORT (0x02, Short.BYTES),
    INT (0x04, Integer.BYTES),
    FLOAT (0x14, Float.BYTES),
    LONG (0x08, Long.BYTES),
    DOUBLE (0x18, Double.BYTES),
    STRING (0x20, Byte.BYTES);

    private final byte value;
    private final int byteSize;

    ParameterType(int value, int byteSize) {
        this.value = (byte)value;
        this.byteSize = byteSize;
    }

    public byte getValue() {
        return value;
    }

    public int getByteSize() {
        return byteSize;
    }

    public static ParameterType fromValue(byte value) {
        for (ParameterType parameterType : ParameterType.values()) {
            if (parameterType.value == value) {
                return parameterType;
            }
        }
        throw new IllegalArgumentException("Unknown parameter type: " + value);
    }
}
