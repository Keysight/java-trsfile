package com.riscure.trs.parameter.traceset;

import com.riscure.trs.TRSMetaDataUtils;
import com.riscure.trs.io.LittleEndianInputStream;
import com.riscure.trs.io.LittleEndianOutputStream;
import com.riscure.trs.types.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public TraceSetParameterMap() {
        super();
    }

    public TraceSetParameterMap(TraceSetParameterMap toCopy) {
        this();
        toCopy.forEach((key, value) -> put(key, value.copy()));
    }

    /**
     * @return a new instance of a TraceParameterMap containing all the same values as this one
     */
    public TraceSetParameterMap copy() {
        return new TraceSetParameterMap(this);
    }

    /**
     * @return this map converted to a byte array, serialized according to the TRS V2 standard definition
     * @throws RuntimeException if the map failed to serialize correctly
     */
    public byte[] serialize() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (LittleEndianOutputStream dos = new LittleEndianOutputStream(baos)) {
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
                LittleEndianInputStream dis = new LittleEndianInputStream(bais);
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
    public <T> Optional<T> get(TypedKey<T> typedKey) {
        TraceSetParameter parameter = get(typedKey.getKey());
        if (parameter != null) {
            if (parameter.getValue().length() == 1 && !typedKey.getCls().isArray()) {
                return Optional.of(typedKey.cast(parameter.getValue().getScalarValue()));
            } else {
                return Optional.of(typedKey.cast(parameter.getValue().getValue()));
            }
        }
        return Optional.empty();
    }

    /**
     * Get a parameter from the map
     * @param typedKey the {@link TypedKey} defining the name and the type of the value to retrieve
     * @param <T> the type of the parameter
     * @return the value of the requested parameter
     * @throws ClassCastException if the requested value is not of the expected type
     * @throws NoSuchElementException if the requested value does not exist in the map
     */
    public <T> T getOrElseThrow(TypedKey<T> typedKey) {
        return get(typedKey).orElseThrow(() -> new NoSuchElementException(String.format(KEY_NOT_FOUND, typedKey.getKey())));
    }

    /**
     * Get a parameter from the map
     * @param typedKey the {@link TypedKey} defining the name and the type of the value to retrieve
     * @param <T> the type of the parameter
     * @param defaultValue the value to use if the value is not present in the map
     * @return the value of the requested parameter
     * @throws ClassCastException if the requested value is not of the expected type
     */
    public <T> T getOrDefault(TypedKey<T> typedKey, T defaultValue) {
        return get(typedKey).orElse(defaultValue);
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
        return getOrElseThrow(new ByteTypeKey(key));
    }

    public byte[] getByteArray(String key) {
        return getOrElseThrow(new ByteArrayTypeKey(key));
    }

    public short getShort(String key) {
        return getOrElseThrow(new ShortTypeKey(key));
    }

    public short[] getShortArray(String key) {
        return getOrElseThrow(new ShortArrayTypeKey(key));
    }

    public int getInt(String key) {
        return getOrElseThrow(new IntegerTypeKey(key));
    }

    public int[] getIntArray(String key) {
        return getOrElseThrow(new IntegerArrayTypeKey(key));
    }

    public float getFloat(String key) {
        return getOrElseThrow(new FloatTypeKey(key));
    }

    public float[] getFloatArray(String key) {
        return getOrElseThrow(new FloatArrayTypeKey(key));
    }

    public long getLong(String key) {
        return getOrElseThrow(new LongTypeKey(key));
    }

    public long[] getLongArray(String key) {
        return getOrElseThrow(new LongArrayTypeKey(key));
    }

    public double getDouble(String key) {
        return getOrElseThrow(new DoubleTypeKey(key));
    }

    public double[] getDoubleArray(String key) {
        return getOrElseThrow(new DoubleArrayTypeKey(key));
    }

    public String getString(String key) {
        return getOrElseThrow(new StringTypeKey(key));
    }

    public boolean getBoolean(String key) {
        return getOrElseThrow(new BooleanTypeKey(key));
    }

    public boolean[] getBooleanArray(String key) {
        return getOrElseThrow(new BooleanArrayTypeKey(key));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TraceSetParameterMap that = (TraceSetParameterMap)o;
        if (this.size() != that.size()) return false;

        return this.entrySet().stream().allMatch(e -> e.getValue().equals(that.get(e.getKey())));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
