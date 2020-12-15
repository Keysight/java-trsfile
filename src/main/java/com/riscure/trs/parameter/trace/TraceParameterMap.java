package com.riscure.trs.parameter.trace;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.*;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinition;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinitionMap;
import com.riscure.trs.types.TypedKey;

import java.io.*;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class TraceParameterMap extends LinkedHashMap<String, TraceParameter> {
    private static final String KEY_NOT_FOUND = "Parameter %s was not found in the trace set.";

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
        put(key, TraceParameterFactory.create(Byte.class, value));
    }

    public void put(String key, byte[] value) {
        put(key, TraceParameterFactory.create(byte[].class, value));
    }

    public void put(String key, short value) {
        put(key, TraceParameterFactory.create(Short.class, value));
    }

    public void put(String key, short[] value) {
        put(key, TraceParameterFactory.create(short[].class, value));
    }

    public void put(String key, int value) {
        put(key, TraceParameterFactory.create(Integer.class, value));
    }

    public void put(String key, int[] value) {
        put(key, TraceParameterFactory.create(int[].class, value));
    }

    public void put(String key, float value) {
        put(key, TraceParameterFactory.create(Float.class, value));
    }

    public void put(String key, float[] value) {
        put(key, TraceParameterFactory.create(float[].class, value));
    }

    public void put(String key, long value) {
        put(key, TraceParameterFactory.create(Long.class, value));
    }

    public void put(String key, long[] value) {
        put(key, TraceParameterFactory.create(long[].class, value));
    }

    public void put(String key, double value) {
        put(key, TraceParameterFactory.create(Double.class, value));
    }

    public void put(String key, double[] value) {
        put(key, TraceParameterFactory.create(double[].class, value));
    }

    public void put(String key, String value) {
        put(key, TraceParameterFactory.create(String.class, value));
    }

    public <T> void put(TypedKey<T> typedKey, T value) {
        put(typedKey.getKey(), TraceParameterFactory.create(typedKey.getCls(), value));
    }

    public <T> T get(TypedKey<T> typedKey) {
        if (get(typedKey.getKey()).length() == 1) {
            return typedKey.getCls().cast(get(typedKey.getKey()).getSimpleValue());
        } else {
            return typedKey.getCls().cast(get(typedKey.getKey()).getValue());
        }
    }

    public byte getByte(String key) {
        if (get(key) != null) {
            return get(key).byteValue();
        }
        throw new RuntimeException(String.format(KEY_NOT_FOUND, key));
    }

    public byte[] getByteArray(String key) {
        if (get(key) != null) {
            return get(key).byteArrayValue();
        }
        throw new RuntimeException(String.format(KEY_NOT_FOUND, key));
    }

    public short getShort(String key) {
        if (get(key) != null) {
            return get(key).shortValue();
        }
        throw new RuntimeException(String.format(KEY_NOT_FOUND, key));
    }

    public short[] getShortArray(String key) {
        if (get(key) != null) {
            return get(key).shortArrayValue();
        }
        throw new RuntimeException(String.format(KEY_NOT_FOUND, key));
    }

    public int getInt(String key) {
        if (get(key) != null) {
            return get(key).intValue();
        }
        throw new RuntimeException(String.format(KEY_NOT_FOUND, key));
    }

    public int[] getIntArray(String key) {
        if (get(key) != null) {
            return get(key).intArrayValue();
        }
        throw new RuntimeException(String.format(KEY_NOT_FOUND, key));
    }

    public float getFloat(String key) {
        if (get(key) != null) {
            return get(key).floatValue();
        }
        throw new RuntimeException(String.format(KEY_NOT_FOUND, key));
    }

    public float[] getFloatArray(String key) {
        if (get(key) != null) {
            return get(key).floatArrayValue();
        }
        throw new RuntimeException(String.format(KEY_NOT_FOUND, key));
    }

    public long getLong(String key) {
        if (get(key) != null) {
            return get(key).longValue();
        }
        throw new RuntimeException(String.format(KEY_NOT_FOUND, key));
    }

    public long[] getLongArray(String key) {
        if (get(key) != null) {
            return get(key).longArrayValue();
        }
        throw new RuntimeException(String.format(KEY_NOT_FOUND, key));
    }

    public double getDouble(String key) {
        if (get(key) != null) {
            return get(key).doubleValue();
        }
        throw new RuntimeException(String.format(KEY_NOT_FOUND, key));
    }

    public double[] getDoubleArray(String key) {
        if (get(key) != null) {
            return get(key).doubleArrayValue();
        }
        throw new RuntimeException(String.format(KEY_NOT_FOUND, key));
    }

    public String getString(String key) {
        if (get(key) != null) {
            return get(key).stringValue();
        }
        throw new RuntimeException(String.format(KEY_NOT_FOUND, key));
    }
}
