package com.riscure.trs.parameter.trace.primitive;

import com.riscure.trs.parameter.trace.TraceParameter;

public class FloatParameter implements TraceParameter {
    private float value;

    public FloatParameter() {}

    public FloatParameter(float value) {
        this.value = value;
    }

    @Override
    public byte[] serialize() {
        int val = Float.floatToIntBits(value);
        return new byte[] {(byte) (val >>> 24 & 0xFF), (byte) (val >>> 16 & 0xFF), (byte) (val >>> 8 & 0xFF), (byte) (val & 0xFF)};
    }

    @Override
    public void deserialize(byte[] bytes) {
        int val = (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
        value = Float.intBitsToFloat(val);
    }

    @Override
    public int length() {
        return Float.BYTES;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FloatParameter that = (FloatParameter) o;

        return Float.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(value);
    }
}
