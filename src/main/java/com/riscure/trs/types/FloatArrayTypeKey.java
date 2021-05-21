package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.FloatArrayParameter;

public class FloatArrayTypeKey extends TypedKey<float[]> {
    public FloatArrayTypeKey(String key) {
        super(float[].class, key);
    }

    @Override
    public TraceParameter createParameter(float[] value) {
        checkLength(value);
        return new FloatArrayParameter(value);
    }
}
