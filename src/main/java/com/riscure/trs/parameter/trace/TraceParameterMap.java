package com.riscure.trs.parameter.trace;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.*;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinition;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinitionMap;
import com.riscure.trs.types.*;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TraceParameterMap extends LinkedHashMap<String, TraceParameter> {
    private static final String KEY_NOT_FOUND = "Parameter %s was not found in the trace set.";
    private static final String EMPTY_DATA_BUT_NONEMPTY_DEFINITIONS = "The provided byte array is null or empty, but the provided definitions are not";
    private static final String DATA_LENGTH_DEFINITIONS_MISMATCH = "The provided byte array (%d bytes) does not match the total definitions length (%d bytes)";

    /**
     * @return a concatenation of all trace parameters in this map, individually converted to byte arrays
     * @throws RuntimeException if the map failed to serialize correctly
     */
    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            for (TraceParameter parameter : values()) {
                parameter.serialize(dos);
            }
            dos.flush();
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param bytes a raw byte array representing the values defined by the definition map
     * @param definitions the type and length information describing the provided byte array
     * @return a new TraceParameterMap, created from the provided byte array based on the provided definitions
     * @throws RuntimeException if the provided byte array does not represent a valid parameter map
     */
    public static TraceParameterMap deserialize(byte[] bytes, TraceParameterDefinitionMap definitions) {
        TraceParameterMap result = new TraceParameterMap();
        if (bytes != null && bytes.length > 0) {
            if (bytes.length != definitions.totalSize()) {
                throw new IllegalArgumentException(String.format(DATA_LENGTH_DEFINITIONS_MISMATCH, bytes.length, definitions.totalSize()));
            }
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                DataInputStream dis = new DataInputStream(bais);
                for (Map.Entry<String, TraceParameterDefinition<TraceParameter>> entry : definitions.entrySet()) {
                    TraceParameter traceParameter = TraceParameter.deserialize(entry.getValue().getType(), entry.getValue().getLength(), dis);
                    result.put(entry.getKey(), traceParameter);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else if (definitions.totalSize() != 0) {
            throw new IllegalArgumentException(EMPTY_DATA_BUT_NONEMPTY_DEFINITIONS);
        }
        return result;
    }

    public <T> void put(TypedKey<T> typedKey, T value) {
        put(typedKey.getKey(), TraceParameterFactory.create(typedKey.getCls(), value));
    }

    public <T> T get(TypedKey<T> typedKey) {
        TraceParameter traceParameter = Optional.ofNullable(get(typedKey.getKey()))
                .orElseThrow(() -> new NoSuchElementException(String.format(KEY_NOT_FOUND, typedKey.getKey())));
        if (traceParameter.length() == 1) {
            return typedKey.cast(traceParameter.getScalarValue());
        } else {
            return typedKey.cast(traceParameter.getValue());
        }
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

    public byte getByte(String key) {
        return get(new ByteTypeKey(key));
    }

    public byte[] getByteArray(String key) {
        return get(new ByteArrayTypeKey(key));
    }

    public short getShort(String key) {
        return get(new ShortTypeKey(key));
    }

    public short[] getShortArray(String key) {
        return get(new ShortArrayTypeKey(key));
    }

    public int getInt(String key) {
        return get(new IntegerTypeKey(key));
    }

    public int[] getIntArray(String key) {
        return get(new IntegerArrayTypeKey(key));
    }

    public float getFloat(String key) {
        return get(new FloatTypeKey(key));
    }

    public float[] getFloatArray(String key) {
        return get(new FloatArrayTypeKey(key));
    }

    public long getLong(String key) {
        return get(new LongTypeKey(key));
    }

    public long[] getLongArray(String key) {
        return get(new LongArrayTypeKey(key));
    }

    public double getDouble(String key) {
        return get(new DoubleTypeKey(key));
    }

    public double[] getDoubleArray(String key) {
        return get(new DoubleArrayTypeKey(key));
    }

    public String getString(String key) {
        return get(new StringTypeKey(key));
    }
}
