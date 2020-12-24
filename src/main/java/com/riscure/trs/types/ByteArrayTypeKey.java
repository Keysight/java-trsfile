package com.riscure.trs.types;

public class ByteArrayTypeKey extends TypedKey<byte[]> {
    public ByteArrayTypeKey(String key) {
        super(byte[].class, key);
    }
}
