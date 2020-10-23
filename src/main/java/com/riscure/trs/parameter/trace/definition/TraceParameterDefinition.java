package com.riscure.trs.parameter.trace.definition;

import com.riscure.trs.parameter.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.*;

public class TraceParameterDefinition<T extends TraceParameter> {
    private final ParameterType type;
    private final short offset;
    private final short length;

    public TraceParameterDefinition(T instance, short offset) {
        this.type = instance.getType();
        this.offset = offset;
        this.length = (short) instance.length();
    }

    public TraceParameterDefinition(ParameterType type, short length, short offset) {
        this.type = type;
        this.offset = offset;
        this.length = length;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeByte(type.getValue());
        dos.writeShort(length);
        dos.writeShort(offset);
    }

    public static TraceParameterDefinition<TraceParameter> deserialize(DataInputStream dis) throws IOException {
        ParameterType type = ParameterType.fromValue(dis.readByte());
        short length = dis.readShort();
        short offset = dis.readShort();
        return new TraceParameterDefinition<>(type, length, offset);
    }

    public ParameterType getType() {
        return type;
    }

    public short getOffset() {
        return offset;
    }

    public short getLength() {
        return length;
    }
}
