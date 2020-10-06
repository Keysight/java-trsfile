package com.riscure.trs.parameter.trace;

import java.io.*;
import java.lang.reflect.Field;

/**
 * This is a utility class for serialization of generic trace parameter classes.
 * Note that this adds significant overhead to every trace because it uses standard Java serialization.
 *
 * It is recommended to implement {@link TraceParameter} over using this class,
 * as it forces you to specify exactly how to serialize your data, and allows to efficiently store and load
 * only the necessary data.
 */
public class GenericTraceParameter implements TraceParameter {
    @Override
    public byte[] serialize() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize parameter", e);
        }
    }

    @Override
    public void deserialize(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            ObjectInputStream ois = new ObjectInputStream(bais);
            copyProperties((GenericTraceParameter) ois.readObject());
        } catch (IOException | ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException("Failed to deserialize parameter", e);
        }
    }

    private void copyProperties(GenericTraceParameter result) throws IllegalAccessException {
        Field[] declaredFields = getClass().getDeclaredFields();
        for (Field field: declaredFields) {
            field.setAccessible(true);
            field.set(this, field.get(result));
        }
    }

    @Override
    public int length() {
        return serialize().length;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
