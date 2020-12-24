package com.riscure.trs.parameter.primitive;

import com.riscure.trs.enums.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.*;
import java.util.Arrays;

public class IntArrayParameter extends TraceParameter {
    private int[] value;

    public IntArrayParameter(int length) {
        value = new int[length];
    }

    public IntArrayParameter(int[] value) {
        this.value = value;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        for (int i : value) {
            dos.writeInt(i);
        }
    }

    public static IntArrayParameter deserialize(DataInputStream dis, int length) throws IOException {
        IntArrayParameter result = new IntArrayParameter(length);
        for (int k = 0; k < length; k++) {
            result.value[k] = dis.readInt();
        }
        return result;
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.INT;
    }

    @Override
    public int[] getValue() {
        return value;
    }

    @Override
    public Integer getScalarValue() {
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

        IntArrayParameter that = (IntArrayParameter) o;

        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
