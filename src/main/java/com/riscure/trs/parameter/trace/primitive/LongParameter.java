package com.riscure.trs.parameter.trace.primitive;

import com.riscure.trs.parameter.trace.TraceParameter;

public class LongParameter implements TraceParameter {
    private long value;

    public LongParameter() {}

    public LongParameter(long value) {
        this.value = value;
    }

    @Override
    public byte[] serialize() {
        return new byte[] {
                (byte) (value >>> 56 & 0xFF), (byte) (value >>> 48 & 0xFF), (byte) (value >>> 40 & 0xFF), (byte) (value >>> 32 & 0xFF),
                (byte) (value >>> 24 & 0xFF), (byte) (value >>> 16 & 0xFF), (byte) (value >>>  8 & 0xFF), (byte) (value        & 0xFF)};
    }

    @Override
    public void deserialize(byte[] bytes) {
        value = ((long)(bytes[0] & 0xFF)) << 56 |
                ((long)(bytes[1] & 0xFF)) << 48 |
                ((long)(bytes[2] & 0xFF)) << 40 |
                ((long)(bytes[3] & 0xFF)) << 32 |
                (bytes[4] & 0xFF) << 24 |
                (bytes[5] & 0xFF) << 16 |
                (bytes[6] & 0xFF) << 8 |
                (bytes[7] & 0xFF);
    }

    @Override
    public int length() {
        return Long.BYTES;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LongParameter that = (LongParameter) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return (int) Double.doubleToLongBits(value);
    }
}
