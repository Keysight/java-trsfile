package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.LongArrayParameter;

public class LongArrayTypeKey extends TypedKey<long[]> {
    public LongArrayTypeKey(String key) {
        super(long[].class, key);
    }

    @Override
    public TraceParameter createParameter(long[] value) {
        checkLength(value);
        return new LongArrayParameter(value);
    }
}
