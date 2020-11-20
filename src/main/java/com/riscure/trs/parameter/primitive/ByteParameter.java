package com.riscure.trs.parameter.primitive;

import com.riscure.trs.parameter.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ByteParameter implements TraceParameter {
    private final byte value;

    public ByteParameter(byte value) {
        this.value = value;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeByte(value);
    }

    public static ByteParameter deserialize(DataInputStream dis) throws IOException {
        return new ByteParameter(dis.readByte());
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.BYTE;
    }

    @Override
    public Byte getValue() {
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

        ByteParameter that = (ByteParameter) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
