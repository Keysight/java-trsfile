package com.riscure.trs.types;

import com.riscure.trs.enums.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.lang.reflect.Array;
import java.util.Objects;

public abstract class TypedKey<T> {
    public static final String INCORRECT_TYPE = "Tried to retrieve a value of type %s, but the actual value was of type %s";

    private final Class<T> cls;
    private final String key;

    protected TypedKey(Class<T> cls, String key) {
        this.cls = cls;
        this.key = key;
    }

    public Class<T> getCls() {
        return cls;
    }

    public String getKey() {
        return key;
    }

    public T cast(Object value) {
        if (cls.isAssignableFrom(value.getClass())) {
            return cls.cast(value);
        }
        throw new ClassCastException(String.format(INCORRECT_TYPE, cls.getName(), value.getClass().getName()));
    }

    public abstract TraceParameter createParameter(T value);

    public ParameterType getType() {
        return ParameterType.fromClass(cls);
    }

    protected void checkLength(T value) {
        if (getCls().isArray() && Array.getLength(value) <= 0) {
            throw new IllegalArgumentException("Array length must be positive and non-zero.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypedKey<?> typedKey = (TypedKey<?>) o;

        if (!Objects.equals(cls, typedKey.cls)) return false;
        return Objects.equals(key, typedKey.key);
    }

    @Override
    public int hashCode() {
        int result = cls != null ? cls.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TypedKey{" +
                "key='" + key +
                "', cls=" + cls +
                '}';
    }
}
