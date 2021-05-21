package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.DoubleArrayParameter;

public class DoubleTypeKey extends TypedKey<Double> {
    public DoubleTypeKey(String key) {
        super(Double.class, key);
    }

    @Override
    public TraceParameter createParameter(Double value) {
        checkLength(value);
        return new DoubleArrayParameter(new double[]{value});
    }
}
