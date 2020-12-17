package com.riscure.trs.parameter;

import com.riscure.trs.enums.ParameterType;
import com.riscure.trs.parameter.primitive.*;
import com.riscure.trs.types.ByteArrayTypeKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This interface represents a parameter that is used in the trace data or the trace set header
 */
public abstract class TraceParameter {
    public static final String SAMPLES = "SAMPLES";
    public static final String TITLE = "TITLE";

    /**
     * The number of values of this type in this parameter
     * @return the number of values of this type in this parameter
     */
    public abstract int length();

    /**
     * @return The type of the parameter.
     */
    public abstract ParameterType getType();

    /**
     * @return The value of the parameter.
     */
    public abstract Object getValue();

    /**
     * @return The value of the parameter as a simple value. Will cause an exception if called on an array type.
     */
    public abstract Object getScalarValue();

    /**
     * Write this TraceParameter to the specified output stream
     * @param dos the OutputStream to write to
     * @throws IOException if any problems arise from writing to the stream
     */
    public abstract void serialize(DataOutputStream dos) throws IOException;

    /**
     * Read a new TraceParameter from the specified input stream
     * @param type the type of the parameter to read
     * @param length the number of values to read
     * @param dis the input stream to read from
     * @return a new TraceParameter of the specified type and length
     * @throws IOException if any problems arise from reading from the stream
     */
    public static TraceParameter deserialize(ParameterType type, short length, DataInputStream dis) throws IOException {
        switch (type) {
            case BYTE:
                return ByteArrayParameter.deserialize(dis, length);
            case SHORT:
                return ShortArrayParameter.deserialize(dis, length);
            case INT:
                return IntArrayParameter.deserialize(dis, length);
            case FLOAT:
                return FloatArrayParameter.deserialize(dis, length);
            case LONG:
                return LongArrayParameter.deserialize(dis, length);
            case DOUBLE:
                return DoubleArrayParameter.deserialize(dis, length);
            case STRING:
                return StringParameter.deserialize(dis, length);
            default:
                throw new IllegalArgumentException("Unknown parameter type: " + type.name());
        }
    }
}
