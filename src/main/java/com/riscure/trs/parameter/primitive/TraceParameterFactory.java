package com.riscure.trs.parameter.primitive;

import com.riscure.trs.parameter.TraceParameter;

public class TraceParameterFactory {
    public static <T> TraceParameter create(Class<T> cls, T value) {
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
}
