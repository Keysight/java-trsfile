package com.riscure.trs.types;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.StringParameter;

public class StringTypeKey extends TypedKey<String> {
    public StringTypeKey(String key) {
        super(String.class, key);
    }

    @Override
    public TraceParameter createParameter(String value) {
        checkLength(value);
        return new StringParameter(value);
    }
}
