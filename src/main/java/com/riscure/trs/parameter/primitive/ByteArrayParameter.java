package com.riscure.trs.parameter.primitive;

import com.riscure.trs.enums.ParameterType;
import com.riscure.trs.HexUtils;
import com.riscure.trs.parameter.TraceParameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ByteArrayParameter extends TraceParameter {
    private static final String INVALID_LENGTH = "Error parsing byte array: Expected (%d) bytes but found (%d)";
    private final byte[] value;

    public ByteArrayParameter(int length) {
        this.value = new byte[length];
    }

    public ByteArrayParameter(byte[] value) {
        this.value = value;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.write(value);
    }

    public static ByteArrayParameter deserialize(DataInputStream dis, int length) throws IOException {
        ByteArrayParameter result = new ByteArrayParameter(length);
        int bytesRead = dis.read(result.value);
        if (bytesRead != length) throw new IOException(String.format(INVALID_LENGTH, length, bytesRead));
        return result;
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.BYTE;
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public Byte getScalarValue() {
        if (length() > 1) throw new IllegalArgumentException("Parameter represents an array value of length " + length());
        return getValue()[0];
    }

    @Override
    public String toString() {
        return HexUtils.toHexString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByteArrayParameter that = (ByteArrayParameter) o;

        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
