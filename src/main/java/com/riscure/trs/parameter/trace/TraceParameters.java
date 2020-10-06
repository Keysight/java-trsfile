package com.riscure.trs.parameter.trace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

public class TraceParameters extends LinkedHashMap<String, TraceParameter> {
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (TraceParameter parameter: values()) {
            baos.write(parameter.serialize());
        }
        return baos.toByteArray();
    }
}
