package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.IntArrayParameter;

public class IntegerArrayTypeKey extends TypedKey<int[]> {
    public IntegerArrayTypeKey(String key) {
        super(int[].class, key);
    }

    @Override
    public TraceParameter createParameter(int[] value) {
        checkLength(value);
        return new IntArrayParameter(value);
    }
}
