package com.riscure.trs.parameter.primitive;

import com.riscure.trs.parameter.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IntParameter implements TraceParameter {
    private final int value;

    public IntParameter(int value) {
        this.value = value;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeInt(value);
    }

    public static IntParameter deserialize(DataInputStream dis) throws IOException {
        return new IntParameter(dis.readInt());
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.INT;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntParameter that = (IntParameter) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
