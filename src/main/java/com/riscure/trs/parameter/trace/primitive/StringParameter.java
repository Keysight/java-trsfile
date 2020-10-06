package com.riscure.trs.parameter.trace.primitive;

import com.riscure.trs.parameter.trace.TraceParameter;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class StringParameter implements TraceParameter {
    private String value;

    public StringParameter() {}

    public StringParameter(String value) {
        this.value = value;
    }

    @Override
    public byte[] serialize() {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void deserialize(byte[] bytes) {
        value = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public int length() {
        return serialize().length;
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
