package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.IntegerArrayParameter;

public class IntegerTypeKey extends TypedKey<Integer> {
    public IntegerTypeKey(String key) {
        super(Integer.class, key);
    }

    @Override
    public TraceParameter createParameter(Integer value) {
        checkLength(value);
        return new IntegerArrayParameter(new int[]{value});
    }
}
