package com.riscure.trs.types;

public class TypedKey<T> {
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
}
