package com.riscure.trs.types;

public class TypedKey<T> {
    private static final String INCORRECT_TYPE = "Tried to retrieve a value of type %s, but the actual value was of type %s";
    private final Class<T> cls;
    private final String key;

    public TypedKey(Class<T> cls, String key) {
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
}
