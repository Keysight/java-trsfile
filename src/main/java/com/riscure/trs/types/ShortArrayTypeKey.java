package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.ShortArrayParameter;

public class ShortArrayTypeKey extends TypedKey<short[]> {
    public ShortArrayTypeKey(String key) {
        super(short[].class, key);
    }

    @Override
    public TraceParameter createParameter(short[] value) {
        checkLength(value);
        return new ShortArrayParameter(value);
    }
}
