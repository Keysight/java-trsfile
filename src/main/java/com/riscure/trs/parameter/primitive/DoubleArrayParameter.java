package com.riscure.trs.parameter.primitive;

import com.riscure.trs.enums.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class DoubleArrayParameter extends TraceParameter {
    private final double[] value;

    public DoubleArrayParameter(int length) {
        value = new double[length];
    }

    public DoubleArrayParameter(double[] value) {
        this.value = value;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        for (double i : value) {
            dos.writeDouble(i);
        }
    }

    public static DoubleArrayParameter deserialize(DataInputStream dis, int length) throws IOException {
        DoubleArrayParameter result = new DoubleArrayParameter(length);
        for (int k = 0; k < length; k++) {
            result.value[k] = dis.readDouble();
        }
        return result;
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.DOUBLE;
    }

    @Override
    public double[] getValue() {
        return value;
    }

    @Override
    public Double getScalarValue() {
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

        DoubleArrayParameter that = (DoubleArrayParameter) o;

        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
