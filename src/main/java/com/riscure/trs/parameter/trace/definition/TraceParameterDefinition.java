package com.riscure.trs.parameter.trace.definition;

import com.riscure.trs.TRSFormatException;
import com.riscure.trs.parameter.trace.TraceParameter;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class TraceParameterDefinition<T extends TraceParameter> implements Serializable {
    private static final String FAILED_TO_INSTANTIATE_TRACE_PARAMETER = "Failed to instantiate TraceParameter. " +
            "Please make sure that your class has a default (0 argument) constructor.";

    private Class<T> type;
    private short offset;
    private short size;

    public TraceParameterDefinition() {}

    public TraceParameterDefinition(T instance, short offset) {
        this.type = (Class<T>) instance.getClass();
        this.offset = offset;
        this.size = (short) instance.length();
    }

    public TraceParameterDefinition(Class<T> type, short offset) throws TRSFormatException {
        this.type = type;
        this.offset = offset;
        try {
            this.size = (short) type.getConstructor().newInstance().serialize().length;
        } catch (InstantiationException | InvocationTargetException |
                NoSuchMethodException | IllegalAccessException e) {
            throw new TRSFormatException(FAILED_TO_INSTANTIATE_TRACE_PARAMETER, e);
        }
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public short getOffset() {
        return offset;
    }

    public void setOffset(short offset) {
        this.offset = offset;
    }

    public short getSize() {
        return size;
    }

    public void setSize(short size) {
        this.size = size;
    }
}
