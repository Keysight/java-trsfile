package com.riscure.trs.parameter.primitive;

import com.riscure.trs.enums.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ShortArrayParameter extends TraceParameter {
    private final short[] value;

    public ShortArrayParameter(int length) {
        value = new short[length];
    }

    public ShortArrayParameter(short[] value) {
        this.value = value;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        for (short i : value) {
            dos.writeShort(i);
        }
    }

    public static ShortArrayParameter deserialize(DataInputStream dis, int length) throws IOException {
        ShortArrayParameter result = new ShortArrayParameter(length);
        for (int k = 0; k < length; k++) {
            result.value[k] = dis.readShort();
        }
        return result;
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.SHORT;
    }

    @Override
    public short[] getValue() {
        return value;
    }

    @Override
    public Short getScalarValue() {
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

        ShortArrayParameter that = (ShortArrayParameter) o;

        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
