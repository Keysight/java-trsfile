package com.riscure.trs.parameter.trace.primitive;

import com.riscure.trs.parameter.trace.TraceParameter;

public class ShortParameter implements TraceParameter {
    private short value;

    public ShortParameter() {}

    public ShortParameter(short value) {
        this.value = value;
    }

    @Override
    public byte[] serialize() {
        return new byte[] {(byte) (value >>> 8 & 0xFF), (byte) (value & 0xFF)};
    }

    @Override
    public void deserialize(byte[] bytes) {
        value = (short) ((bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF));
    }

    @Override
    public int length() {
        return Short.BYTES;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShortParameter that = (ShortParameter) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
