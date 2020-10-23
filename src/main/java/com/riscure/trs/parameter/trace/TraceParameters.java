package com.riscure.trs.parameter.trace;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.*;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinition;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinitions;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TraceParameters extends LinkedHashMap<String, TraceParameter> {
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        for (TraceParameter parameter: values()) {
            ParameterUtils.serialize(parameter, dos);
        }
        dos.flush();
        return baos.toByteArray();
    }

    public static TraceParameters deserialize(byte[] bytes, TraceParameterDefinitions definitions) throws IOException {
        TraceParameters result = new TraceParameters();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            DataInputStream dis = new DataInputStream(bais);
            for (Map.Entry<String, TraceParameterDefinition<TraceParameter>> entry : definitions.entrySet()) {
                TraceParameter traceParameter = ParameterUtils.deserialize(entry.getValue().getType(), entry.getValue().getLength(), dis);
                result.put(entry.getKey(), traceParameter);
            }
        }
        return result;
    }

    public void put(String key, byte value) {
        put(key, new ByteParameter(value));
    }

    public void put(String key, byte[] value) {
        put(key, new ByteArrayParameter(value));
    }

    public void put(String key, short value) {
        put(key, new ShortParameter(value));
    }

    public void put(String key, short[] value) {
        put(key, new ShortArrayParameter(value));
    }

    public void put(String key, int value) {
        put(key, new IntParameter(value));
    }

    public void put(String key, int[] value) {
        put(key, new IntArrayParameter(value));
    }

    public void put(String key, float value) {
        put(key, new FloatParameter(value));
    }

    public void put(String key, float[] value) {
        put(key, new FloatArrayParameter(value));
    }

    public void put(String key, long value) {
        put(key, new LongParameter(value));
    }

    public void put(String key, long[] value) {
        put(key, new LongArrayParameter(value));
    }

    public void put(String key, double value) {
        put(key, new DoubleParameter(value));
    }

    public void put(String key, double[] value) {
        put(key, new DoubleArrayParameter(value));
    }

    public void put(String key, String value) {
        put(key, new StringParameter(value));
    }
}
