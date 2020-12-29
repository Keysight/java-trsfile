package com.riscure.trs.parameter.traceset;

import com.riscure.trs.types.*;
import com.riscure.trs.parameter.primitive.*;

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

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
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
    }

    public static TraceSetParameterMap deserialize(byte[] bytes) throws IOException {
        TraceSetParameterMap result = new TraceSetParameterMap();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            DataInputStream dis = new DataInputStream(bais);
            //Read NE
            short numberOfEntries = dis.readShort();
            for (int k = 0; k < numberOfEntries; k++) {
                //Read NL
                short nameLength = dis.readShort();
                byte[] nameBytes = new byte[nameLength];
                int read = dis.read(nameBytes, 0, nameLength);
                if (read != nameLength) throw new IOException("Error reading parameter name");
                //Read N
                String name = new String(nameBytes, StandardCharsets.UTF_8);
                //Read value
                result.put(name, TraceSetParameter.deserialize(dis));
            }
        }
        return result;
    }

    public void put(String key, byte value) {
        put(key, new TraceSetParameter(TraceParameterFactory.create(Byte.class, value)));
    }

    public void put(String key, byte[] value) {
        put(key, new TraceSetParameter(TraceParameterFactory.create(byte[].class, value)));
    }

    public void put(String key, short value) {
        put(key, new TraceSetParameter(TraceParameterFactory.create(Short.class, value)));
    }

    public void put(String key, short[] value) {
        put(key, new TraceSetParameter(TraceParameterFactory.create(short[].class, value)));
    }

    public void put(String key, int value) {
        put(key, new TraceSetParameter(TraceParameterFactory.create(Integer.class, value)));
    }

    public void put(String key, int[] value) {
        put(key, new TraceSetParameter(TraceParameterFactory.create(int[].class, value)));
    }

    public void put(String key, float value) {
        put(key, new TraceSetParameter(TraceParameterFactory.create(Float.class, value)));
    }

    public void put(String key, float[] value) {
        put(key, new TraceSetParameter(TraceParameterFactory.create(float[].class, value)));
    }

    public void put(String key, long value) {
        put(key, new TraceSetParameter(TraceParameterFactory.create(Long.class, value)));
    }

    public void put(String key, long[] value) {
        put(key, new TraceSetParameter(TraceParameterFactory.create(long[].class, value)));
    }

    public void put(String key, double value) {
        put(key, new TraceSetParameter(TraceParameterFactory.create(Double.class, value)));
    }

    public void put(String key, double[] value) {
        put(key, new TraceSetParameter(TraceParameterFactory.create(double[].class, value)));
    }

    public void put(String key, String value) {
        put(key, new TraceSetParameter(TraceParameterFactory.create(String.class, value)));
    }

    public <T> void put(TypedKey<T> typedKey, T value) {
        put(typedKey.getKey(), new TraceSetParameter(TraceParameterFactory.create(typedKey.getCls(), value)));
    }

    public <T> T get(TypedKey<T> typedKey) {
        TraceSetParameter traceSetParameter = Optional.ofNullable(get(typedKey.getKey()))
                .orElseThrow(() -> new NoSuchElementException(String.format(KEY_NOT_FOUND, typedKey.getKey())));
        if (traceSetParameter.getValue().length() == 1) {
            return typedKey.cast(traceSetParameter.getValue().getScalarValue());
        } else {
            return typedKey.cast(traceSetParameter.getValue().getValue());
        }
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
