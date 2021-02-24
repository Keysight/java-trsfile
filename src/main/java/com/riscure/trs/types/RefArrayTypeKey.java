package com.riscure.trs.types;

import com.riscure.trs.parameter.primitive.Ref;
import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.RefArrayParameter;

public class RefArrayTypeKey extends TypedKey<Ref[]> {
    public RefArrayTypeKey(String key) {
        super(Ref[].class, key);
    }

    @Override
    public TraceParameter createParameter(Ref[] value) {
        checkLength(value);
        return new RefArrayParameter(value);
    }
}
