package com.riscure.trs.parameter.primitive;

import com.riscure.trs.enums.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.*;
import java.util.Arrays;

public class FloatArrayParameter extends TraceParameter {
    private final float[] value;

    public FloatArrayParameter(int length) {
        value = new float[length];
    }

    public FloatArrayParameter(float[] value) {
        this.value = value;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        for (float i : value) {
            dos.writeFloat(i);
        }
    }

    public static FloatArrayParameter deserialize(DataInputStream dis, int length) throws IOException {
        FloatArrayParameter result = new FloatArrayParameter(length);
        for (int k = 0; k < length; k++) {
            result.value[k] = dis.readFloat();
        }
        return result;
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.FLOAT;
    }

    @Override
    public float[] getValue() {
        return value;
    }

    @Override
    public Float getScalarValue() {
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

        FloatArrayParameter that = (FloatArrayParameter) o;

        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
