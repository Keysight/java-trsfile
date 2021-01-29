package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.BooleanArrayParameter;

public class BooleanTypeKey extends TypedKey<Boolean> {
    public BooleanTypeKey(String key) {
        super(Boolean.class, key);
    }

    @Override
    public TraceParameter createParameter(Boolean value) {
        checkLength(value);
        return new BooleanArrayParameter(new boolean[]{value});
    }
}
