package com.riscure.trs.parameter.primitive;

import com.riscure.trs.enums.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class IntegerArrayParameter extends TraceParameter {
    private final int[] value;

    public IntegerArrayParameter(int length) {
        value = new int[length];
    }

    public IntegerArrayParameter(int[] value) {
        this.value = value;
    }

    public IntegerArrayParameter(IntegerArrayParameter toCopy) {
        this(toCopy.getValue().clone());
    }

    public void serialize(DataOutputStream dos) throws IOException {
        for (int i : value) {
            dos.writeInt(i);
        }
    }

    public static IntegerArrayParameter deserialize(DataInputStream dis, int length) throws IOException {
        IntegerArrayParameter result = new IntegerArrayParameter(length);
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
    public IntegerArrayParameter copy() {
        return new IntegerArrayParameter(this);
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

        IntegerArrayParameter that = (IntegerArrayParameter) o;

        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
