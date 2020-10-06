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
    private short length;

    public TraceParameterDefinition() {}

    public TraceParameterDefinition(Class<T> type, short offset) throws TRSFormatException {
        this.type = type;
        this.offset = offset;
        try {
            this.length = (short) type.getConstructor().newInstance().serialize().length;
        } catch (IOException | InstantiationException | InvocationTargetException |
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

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }
}
