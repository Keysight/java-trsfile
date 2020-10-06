package com.riscure.trs.parameter.trace.primitive;

import com.riscure.trs.parameter.trace.TraceParameter;

public class IntParameter implements TraceParameter {
    private int value;

    public IntParameter() {}

    public IntParameter(int value) {
        this.value = value;
    }

    @Override
    public byte[] serialize() {
        return new byte[] {(byte) (value >>> 24 & 0xFF), (byte) (value >>> 16 & 0xFF), (byte) (value >>> 8 & 0xFF), (byte) (value & 0xFF)};
    }

    @Override
    public void deserialize(byte[] bytes) {
        value = (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    @Override
    public int length() {
        return Integer.BYTES;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntParameter that = (IntParameter) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
