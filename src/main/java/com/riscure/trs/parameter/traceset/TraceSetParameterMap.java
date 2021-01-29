package com.riscure.trs.parameter.traceset;

import com.riscure.trs.TRSMetaDataUtils;
import com.riscure.trs.types.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * This class represents the header definitions of all user-added global parameters of the trace set
 *
 * This explicitly implements LinkedHashMap to ensure that the data is retrieved in the same order as it was added
 */
public class TraceSetParameterMap extends LinkedHashMap<String, TraceSetParameter> {
    private static final String KEY_NOT_FOUND = "Parameter %s was not found in the trace set.";

    /**
     * @return this map converted to a byte array, serialized according to the TRS V2 standard definition
     * @throws RuntimeException if the map failed to serialize correctly
     */
    public byte[] serialize() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            //Write NE
            dos.writeShort(size());
            for (Map.Entry<String, TraceSetParameter> entry : entrySet()) {
                byte[] nameBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
                //Write NL
                dos.writeShort(nameBytes.length);
                //Write N
                dos.write(nameBytes);
                //Write value
                entry.getValue().serialize(dos);
            }
            dos.flush();
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param bytes a valid serialized Trace set parameter map
     * @return a new populated Trace set parameter map as represented by the provided byte array
     * @throws RuntimeException if the provided byte array does not represent a valid parameter map
     */
    public static TraceSetParameterMap deserialize(byte[] bytes) {
        TraceSetParameterMap result = new TraceSetParameterMap();
        if (bytes != null && bytes.length > 0) {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                DataInputStream dis = new DataInputStream(bais);
                //Read NE
                short numberOfEntries = dis.readShort();
                for (int k = 0; k < numberOfEntries; k++) {
                    String name = TRSMetaDataUtils.readName(dis);
                    //Read value
                    result.put(name, TraceSetParameter.deserialize(dis));
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return UnmodifiableTraceSetParameterMap.of(result);
    }

    /**
     * @param key the typed key to check for
     * @param <T> the type of the parameter
     * @return whether the key is present in the map AND the parameter type matches the requested key's type
     */
    public <T> boolean containsKey(TypedKey<T> key) {
        boolean containsKey = super.containsKey(key.getKey());
        if (containsKey) return get(key).getClass().equals(key.getCls());
        return false;
    }

    /**
     * Add a new parameter to the map
     * @param typedKey the {@link TypedKey} defining the name and the type of the added value
     * @param value the value of the parameter to add
     * @param <T> the type of the parameter
     * @throws IllegalArgumentException if the value is not valid
     */
    public <T> void put(TypedKey<T> typedKey, T value) {
        put(typedKey.getKey(), new TraceSetParameter(typedKey.createParameter(value)));
    }

    /**
     * Get a parameter from the map
     * @param typedKey the {@link TypedKey} defining the name and the type of the value to retrieve
     * @param <T> the type of the parameter
     * @return the value of the requested parameter
     * @throws ClassCastException if the requested value is not of the expected type
     */
    public <T> T get(TypedKey<T> typedKey) {
        TraceSetParameter traceSetParameter = Optional.ofNullable(get(typedKey.getKey()))
                .orElseThrow(() -> new NoSuchElementException(String.format(KEY_NOT_FOUND, typedKey.getKey())));
        if (traceSetParameter.getValue().length() == 1) {
            return typedKey.cast(traceSetParameter.getValue().getScalarValue());
        } else {
            return typedKey.cast(traceSetParameter.getValue().getValue());
        }
    }

    public void put(String key, byte value) {
        put(new ByteTypeKey(key), value);
    }

    public void put(String key, byte[] value) {
        put(new ByteArrayTypeKey(key), value);
    }

    public void put(String key, short value) {
        put(new ShortTypeKey(key), value);
    }

    public void put(String key, short[] value) {
        put(new ShortArrayTypeKey(key), value);
    }

    public void put(String key, int value) {
        put(new IntegerTypeKey(key), value);
    }

    public void put(String key, int[] value) {
        put(new IntegerArrayTypeKey(key), value);
    }

    public void put(String key, float value) {
        put(new FloatTypeKey(key), value);
    }

    public void put(String key, float[] value) {
        put(new FloatArrayTypeKey(key), value);
    }

    public void put(String key, long value) {
        put(new LongTypeKey(key), value);
    }

    public void put(String key, long[] value) {
        put(new LongArrayTypeKey(key), value);
    }

    public void put(String key, double value) {
        put(new DoubleTypeKey(key), value);
    }

    public void put(String key, double[] value) {
        put(new DoubleArrayTypeKey(key), value);
    }

    public void put(String key, String value) {
        put(new StringTypeKey(key), value);
    }

    public void put(String key, boolean value) {
        put(new BooleanTypeKey(key), value);
    }

    public void put(String key, boolean[] value) {
        put(new BooleanArrayTypeKey(key), value);
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

    public boolean getBoolean(String key) {
        return get(new BooleanTypeKey(key));
    }

    public boolean[] getBooleanArray(String key) {
        return get(new BooleanArrayTypeKey(key));
    }
}
