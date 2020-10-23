package com.riscure.trs.parameter.primitive;

import com.riscure.trs.parameter.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class StringParameter implements TraceParameter {
    private final String value;

    public StringParameter(String value) {
        this.value = value;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.write(value.getBytes(StandardCharsets.UTF_8));
    }

    public static StringParameter deserialize(DataInputStream dis, int length) throws IOException {
        byte[] bytes = new byte[length];
        int read = dis.read(bytes);
        if (read != length) throw new IOException("Failed to read string parameter");
        return new StringParameter(new String(bytes, StandardCharsets.UTF_8));
    }

    @Override
    public int length() {
        return value.getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.STRING;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringParameter that = (StringParameter) o;

        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
