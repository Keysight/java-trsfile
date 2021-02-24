package com.riscure.trs.parameter.primitive;

import com.riscure.trs.enums.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class RefArrayParameter extends TraceParameter {
    private final Ref[] value;

    public RefArrayParameter(int length) {
        value = new Ref[length];
    }

    public RefArrayParameter(Ref[] value) {
        this.value = value;
    }

    public RefArrayParameter(RefArrayParameter toCopy) {
        this(toCopy.getValue().clone());
    }

    public void serialize(DataOutputStream dos) throws IOException {
        for (Ref i : value) {
            dos.writeInt(i.getIndex());
        }
    }

    public static RefArrayParameter deserialize(DataInputStream dis, String[] values, int length) throws IOException {
        RefArrayParameter result = new RefArrayParameter(length);
        for (int k = 0; k < length; k++) {
            int index = dis.readInt();
            result.value[k] = new Ref(values[index], index);
        }
        return result;
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.REF;
    }

    @Override
    public Ref[] getValue() {
        return value;
    }

    @Override
    public RefArrayParameter copy() {
        return new RefArrayParameter(this);
    }

    @Override
    public Ref getScalarValue() {
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

        RefArrayParameter that = (RefArrayParameter) o;

        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
