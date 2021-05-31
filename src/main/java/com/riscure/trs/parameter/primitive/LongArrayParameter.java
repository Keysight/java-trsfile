package com.riscure.trs.parameter.primitive;

import com.riscure.trs.enums.ParameterType;
import com.riscure.trs.io.LittleEndianInputStream;
import com.riscure.trs.io.LittleEndianOutputStream;
import com.riscure.trs.parameter.TraceParameter;

import java.io.IOException;
import java.util.Arrays;

public class LongArrayParameter extends TraceParameter {
    private final long[] value;

    public LongArrayParameter(int length) {
        value = new long[length];
    }

    public LongArrayParameter(long[] value) {
        this.value = value;
    }

    public LongArrayParameter(LongArrayParameter toCopy) {
        this(toCopy.getValue().clone());
    }

    public void serialize(LittleEndianOutputStream dos) throws IOException {
        for (long i : value) {
            dos.writeLong(i);
        }
    }

    public static LongArrayParameter deserialize(LittleEndianInputStream dis, int length) throws IOException {
        LongArrayParameter result = new LongArrayParameter(length);
        for (int k = 0; k < length; k++) {
            result.value[k] = dis.readLong();
        }
        return result;
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.LONG;
    }

    @Override
    public long[] getValue() {
        return value;
    }

    @Override
    public LongArrayParameter copy() {
        return new LongArrayParameter(this);
    }

    @Override
    public Long getScalarValue() {
        if (length() > 1) throw new IllegalArgumentException("Parameter represents an array value of length " + length());
        return getValue()[0];
    }

    @Override
    public String toString() {
        return Arrays.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LongArrayParameter that = (LongArrayParameter) o;

        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
