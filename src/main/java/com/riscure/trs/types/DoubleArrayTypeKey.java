package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.DoubleArrayParameter;

public class DoubleArrayTypeKey extends TypedKey<double[]> {
    public DoubleArrayTypeKey(String key) {
        super(double[].class, key);
    }

    @Override
    public TraceParameter createParameter(double[] value) {
        checkLength(value);
        return new DoubleArrayParameter(value);
    }
}
