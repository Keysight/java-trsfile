package com.riscure.trs.parameter.primitive;

import com.riscure.trs.parameter.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ShortParameter implements TraceParameter {
    private final short value;

    public ShortParameter(short value) {
        this.value = value;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeShort(value);
    }

    public static ShortParameter deserialize(DataInputStream dis) throws IOException {
        return new ShortParameter(dis.readShort());
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.SHORT;
    }

    @Override
    public Short getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShortParameter that = (ShortParameter) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
