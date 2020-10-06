package com.riscure.trs.parameter.trace.primitive;

import com.riscure.trs.parameter.trace.TraceParameter;

public class ByteParameter implements TraceParameter {
    private byte value;

    public ByteParameter() {}

    public ByteParameter(byte value) {
        this.value = value;
    }

    @Override
    public byte[] serialize() {
        return new byte[] {value};
    }

    @Override
    public void deserialize(byte[] bytes) {
        value = bytes[0];
    }

    @Override
    public int length() {
        return Byte.BYTES;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByteParameter that = (ByteParameter) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
