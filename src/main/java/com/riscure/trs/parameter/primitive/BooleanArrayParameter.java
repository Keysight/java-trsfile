package com.riscure.trs.parameter.primitive;

import com.riscure.trs.enums.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class BooleanArrayParameter extends TraceParameter {
    private final boolean[] value;

    public BooleanArrayParameter(int length) {
        value = new boolean[length];
    }

    public BooleanArrayParameter(boolean[] value) {
        this.value = value;
    }

    public BooleanArrayParameter(BooleanArrayParameter toCopy) {
        this(toCopy.getValue().clone());
    }

    public void serialize(DataOutputStream dos) throws IOException {
        for (boolean i : value) {
            dos.writeByte(i ? 1 : 0);
        }
    }

    public static BooleanArrayParameter deserialize(DataInputStream dis, int length) throws IOException {
        BooleanArrayParameter result = new BooleanArrayParameter(length);
        for (int k = 0; k < length; k++) {
            result.value[k] = dis.readByte() != 0;
        }
        return result;
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.BOOL;
    }

    @Override
    public boolean[] getValue() {
        return value;
    }

    @Override
    public BooleanArrayParameter copy() {
        return new BooleanArrayParameter(this);
    }

    @Override
    public Boolean getScalarValue() {
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

        BooleanArrayParameter that = (BooleanArrayParameter) o;

        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
