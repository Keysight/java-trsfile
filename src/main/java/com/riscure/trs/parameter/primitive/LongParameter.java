package com.riscure.trs.parameter.primitive;

import com.riscure.trs.parameter.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LongParameter implements TraceParameter {
    private final long value;

    public LongParameter(long value) {
        this.value = value;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeLong(value);
    }

    public static LongParameter deserialize(DataInputStream dis) throws IOException {
        return new LongParameter(dis.readLong());
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.LONG;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LongParameter that = (LongParameter) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return (int) Double.doubleToLongBits(value);
    }
}
