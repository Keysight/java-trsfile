package com.riscure.trs.parameter.primitive;

import com.riscure.trs.parameter.TraceParameter;

import java.lang.reflect.Array;

public class TraceParameterFactory {
    /**
     * Create a new TraceParameter from the provided type and value
     * @param cls the class of the type of the parameter
     * @param value the value of the parameter
     * @param <T> the generic type of the parameter
     * @return a new TraceParameter
     */
    public static <T> TraceParameter create(Class<T> cls, T value) {
        checkLength(cls, value);
        if (Byte.class.isAssignableFrom(cls)) {
            return new ByteArrayParameter(new byte[]{(Byte)value});
        } else if (byte[].class.isAssignableFrom(cls)) {
            return new ByteArrayParameter((byte[])value);
        } else if (Short.class.isAssignableFrom(cls)) {
            return new ShortArrayParameter(new short[]{(Short)value});
        } else if (short[].class.isAssignableFrom(cls)) {
            return new ShortArrayParameter((short[])value);
        } else if (Integer.class.isAssignableFrom(cls)) {
            return new IntArrayParameter(new int[]{(Integer)value});
        } else if (int[].class.isAssignableFrom(cls)) {
            return new IntArrayParameter((int[])value);
        } else if (Float.class.isAssignableFrom(cls)) {
            return new FloatArrayParameter(new float[]{(Float)value});
        } else if (float[].class.isAssignableFrom(cls)) {
            return new FloatArrayParameter((float[])value);
        } else if (Long.class.isAssignableFrom(cls)) {
            return new LongArrayParameter(new long[]{(Long)value});
        } else if (long[].class.isAssignableFrom(cls)) {
            return new LongArrayParameter((long[])value);
        } else if (Double.class.isAssignableFrom(cls)) {
            return new DoubleArrayParameter(new double[]{(Double)value});
        } else if (double[].class.isAssignableFrom(cls)) {
            return new DoubleArrayParameter((double[])value);
        } else if (String.class.isAssignableFrom(cls)) {
            return new StringParameter((String)value);
        } else {
            throw new RuntimeException("Unsupported class: " + cls.getName());
        }
    }

    public static <T> void checkLength(Class<T> cls, T value) {
        if (cls.isArray() && Array.getLength(value) <= 0) {
            throw new IllegalArgumentException("Array length must be positive and non-zero.");
        }
    }

    private TraceParameterFactory() {}
}
