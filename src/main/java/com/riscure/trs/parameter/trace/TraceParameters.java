package com.riscure.trs.parameter.trace;

import com.riscure.trs.parameter.trace.primitive.*;

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

    public void put(String key, byte value) {
        put(key, new ByteParameter(value));
    }

    public void put(String key, short value) {
        put(key, new ShortParameter(value));
    }

    public void put(String key, int value) {
        put(key, new IntParameter(value));
    }

    public void put(String key, float value) {
        put(key, new FloatParameter(value));
    }

    public void put(String key, long value) {
        put(key, new LongParameter(value));
    }

    public void put(String key, double value) {
        put(key, new DoubleParameter(value));
    }

    public void put(String key, String value) {
        put(key, new StringParameter(value));
    }

    public void put(String key, byte[] value) {
        put(key, new ByteArrayParameter(value));
    }
}
