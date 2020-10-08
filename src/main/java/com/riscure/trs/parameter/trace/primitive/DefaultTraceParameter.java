package com.riscure.trs.parameter.trace.primitive;

import com.riscure.trs.parameter.trace.TraceParameter;

import java.io.*;
import java.util.Objects;

/**
 * This is a utility class for serialization of generic trace parameter classes.
 * Note that this adds significant overhead to every trace because it uses standard Java serialization.
 *
 * It is recommended to implement {@link TraceParameter} over using this class,
 * as it forces you to specify exactly how to serialize your data, and allows to efficiently store and load
 * only the necessary data.
 */
public class DefaultTraceParameter implements TraceParameter {
    private Serializable value;

    public DefaultTraceParameter() {}

    public DefaultTraceParameter(Serializable value) {
        this.value = value;
    }

    @Override
    public byte[] serialize() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            oos.flush();
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to serialize parameter", ex);
        }
    }

    @Override
    public void deserialize(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            ObjectInputStream ois = new ObjectInputStream(bais);
            value = (Serializable) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException("Failed to deserialize parameter", ex);
        }
    }

    @Override
    public int length() {
        return serialize().length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultTraceParameter that = (DefaultTraceParameter) o;

        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
