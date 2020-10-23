package com.riscure.trs.parameter.primitive;

import com.riscure.trs.parameter.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FloatParameter implements TraceParameter {
    private float value;

    public FloatParameter() {}

    public FloatParameter(float value) {
        this.value = value;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeFloat(value);
    }

    public static FloatParameter deserialize(DataInputStream dis) throws IOException {
        return new FloatParameter(dis.readFloat());
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.FLOAT;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FloatParameter that = (FloatParameter) o;

        return Float.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(value);
    }
}
