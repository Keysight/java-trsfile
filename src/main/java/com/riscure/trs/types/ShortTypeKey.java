package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.ShortArrayParameter;

public class ShortTypeKey extends TypedKey<Short> {
    public ShortTypeKey(String key) {
        super(Short.class, key);
    }

    @Override
    public TraceParameter createParameter(Short value) {
        checkLength(value);
        return new ShortArrayParameter(new short[]{value});
    }
}
