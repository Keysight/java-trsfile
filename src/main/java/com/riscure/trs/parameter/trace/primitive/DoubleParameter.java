package com.riscure.trs.parameter.trace.primitive;

import com.riscure.trs.parameter.trace.TraceParameter;

public class DoubleParameter implements TraceParameter {
    private double value;

    public DoubleParameter() {}

    public DoubleParameter(double value) {
        this.value = value;
    }

    @Override
    public byte[] serialize() {
        long val = Double.doubleToLongBits(value);
        return new byte[] {
                (byte) (val >>> 56 & 0xFF), (byte) (val >>> 48 & 0xFF), (byte) (val >>> 40 & 0xFF), (byte) (val >>> 32 & 0xFF),
                (byte) (val >>> 24 & 0xFF), (byte) (val >>> 16 & 0xFF), (byte) (val >>>  8 & 0xFF), (byte) (val        & 0xFF)};
    }

    @Override
    public void deserialize(byte[] bytes) {
        long val = ((long)(bytes[0] & 0xFF)) << 56 |
                ((long)(bytes[1] & 0xFF)) << 48 |
                ((long)(bytes[2] & 0xFF)) << 40 |
                ((long)(bytes[3] & 0xFF)) << 32 |
                (bytes[4] & 0xFF) << 24 |
                (bytes[5] & 0xFF) << 16 |
                (bytes[6] & 0xFF) << 8 |
                (bytes[7] & 0xFF);
        value = Double.longBitsToDouble(val);
    }

    @Override
    public int length() {
        return Double.BYTES;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubleParameter that = (DoubleParameter) o;

        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return (int) Double.doubleToLongBits(value);
    }
}
