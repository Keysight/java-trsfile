package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.LongArrayParameter;

public class LongTypeKey extends TypedKey<Long> {
    public LongTypeKey(String key) {
        super(Long.class, key);
    }

    @Override
    public TraceParameter createParameter(Long value) {
        checkLength(value);
        return new LongArrayParameter(new long[]{value});
    }
}
