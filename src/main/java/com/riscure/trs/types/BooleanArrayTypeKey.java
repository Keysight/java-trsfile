package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.BooleanArrayParameter;

public class BooleanArrayTypeKey extends TypedKey<boolean[]> {
    public BooleanArrayTypeKey(String key) {
        super(boolean[].class, key);
    }

    @Override
    public TraceParameter createParameter(boolean[] value) {
        checkLength(value);
        return new BooleanArrayParameter(value);
    }
}
