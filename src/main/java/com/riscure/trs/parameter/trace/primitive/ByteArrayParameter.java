package com.riscure.trs.parameter.trace.primitive;

import com.riscure.trs.parameter.trace.TraceParameter;

import java.util.Arrays;

public class ByteArrayParameter implements TraceParameter {
    private byte[] value;

    public ByteArrayParameter() {}

    public ByteArrayParameter(byte[] value) {
        this.value = value;
    }

    @Override
    public byte[] serialize() {
        return value;
    }

    @Override
    public void deserialize(byte[] bytes) {
        value = new byte[bytes.length];
        System.arraycopy(bytes, 0, value, 0, bytes.length);
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByteArrayParameter that = (ByteArrayParameter) o;

        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
