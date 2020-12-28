package com.riscure.trs.enums;

public enum ParameterType {
    BYTE (0x01, Byte.BYTES, Byte.class, byte[].class),
    SHORT (0x02, Short.BYTES, Short.class, short[].class),
    INT (0x04, Integer.BYTES, Integer.class, int[].class),
    FLOAT (0x14, Float.BYTES, Float.class, float[].class),
    LONG (0x08, Long.BYTES, Long.class, long[].class),
    DOUBLE (0x18, Double.BYTES, Double.class, double[].class),
    STRING (0x20, Byte.BYTES, String.class, String.class);

    private final byte value;
    private final int byteSize;
    private final Class<?> cls;
    private final Class<?> arrayCls;

    ParameterType(int value, int byteSize, Class<?> cls, Class<?> arrayCls) {
        this.value = (byte)value;
        this.byteSize = byteSize;
        this.cls = cls;
        this.arrayCls = arrayCls;
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

    public static ParameterType fromClass(Class<?> cls) {
        for (ParameterType parameterType : ParameterType.values()) {
            if (parameterType.cls.isAssignableFrom(cls) || parameterType.arrayCls.isAssignableFrom(cls)) {
                return parameterType;
            }
        }
        throw new IllegalArgumentException("Unknown parameter type: " + cls);
    }
}
