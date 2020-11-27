package com.riscure.trs.parameter.trace;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.*;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinition;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinitionMap;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TraceParameterMap extends LinkedHashMap<String, TraceParameter> {
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        for (TraceParameter parameter: values()) {
            parameter.serialize(dos);
        }
        dos.flush();
        return baos.toByteArray();
    }

    public static TraceParameterMap deserialize(byte[] bytes, TraceParameterDefinitionMap definitions) throws IOException {
        TraceParameterMap result = new TraceParameterMap();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            DataInputStream dis = new DataInputStream(bais);
            for (Map.Entry<String, TraceParameterDefinition<TraceParameter>> entry : definitions.entrySet()) {
                TraceParameter traceParameter = TraceParameter.deserialize(entry.getValue().getType(), entry.getValue().getLength(), dis);
                result.put(entry.getKey(), traceParameter);
            }
        }
        return result;
    }

    public void put(String key, byte value) {
        put(key, new byte[]{value});
    }

    public void put(String key, byte[] value) {
        put(key, new ByteArrayParameter(value));
    }

    public void put(String key, short value) {
        put(key, new short[]{value});
    }

    public void put(String key, short[] value) {
        put(key, new ShortArrayParameter(value));
    }

    public void put(String key, int value) {
        put(key, new int[]{value});
    }

    public void put(String key, int[] value) {
        put(key, new IntArrayParameter(value));
    }

    public void put(String key, float value) {
        put(key, new float[]{value});
    }

    public void put(String key, float[] value) {
        put(key, new FloatArrayParameter(value));
    }

    public void put(String key, long value) {
        put(key, new long[]{value});
    }

    public void put(String key, long[] value) {
        put(key, new LongArrayParameter(value));
    }

    public void put(String key, double value) {
        put(key, new double[]{value});
    }

    public void put(String key, double[] value) {
        put(key, new DoubleArrayParameter(value));
    }

    public void put(String key, String value) {
        put(key, new StringParameter(value));
    }
}
