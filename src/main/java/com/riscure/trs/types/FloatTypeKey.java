package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.FloatArrayParameter;

public class FloatTypeKey extends TypedKey<Float> {
    public FloatTypeKey(String key) {
        super(Float.class, key);
    }

    @Override
    public TraceParameter createParameter(Float value) {
        checkLength(value);
        return new FloatArrayParameter(new float[]{value});
    }
}
