package com.riscure.trs.parameter.primitive;

import com.riscure.trs.parameter.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleParameter implements TraceParameter {
    private final double value;

    public DoubleParameter(double value) {
        this.value = value;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeDouble(value);
    }

    public static DoubleParameter deserialize(DataInputStream dis) throws IOException {
        return new DoubleParameter(dis.readDouble());
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.DOUBLE;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubleParameter that = (DoubleParameter) o;

        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return (int) Double.doubleToLongBits(value);
    }
}
