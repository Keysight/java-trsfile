package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.ByteArrayParameter;

public class ByteTypeKey extends TypedKey<Byte> {
    public ByteTypeKey(String key) {
        super(Byte.class, key);
    }

    @Override
    public TraceParameter createParameter(Byte value) {
        checkLength(value);
        return new ByteArrayParameter(new byte[]{value});
    }
}
