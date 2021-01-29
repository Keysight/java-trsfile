package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.ByteArrayParameter;

public class ByteArrayTypeKey extends TypedKey<byte[]> {
    public ByteArrayTypeKey(String key) {
        super(byte[].class, key);
    }

    @Override
    public TraceParameter createParameter(byte[] value) {
        checkLength(value);
        return new ByteArrayParameter(value);
    }
}
