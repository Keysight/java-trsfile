package com.riscure.trs.parameter.trace.definition;

import com.riscure.trs.parameter.trace.TraceParameter;

import java.io.*;
import java.util.LinkedHashMap;

/**
 * This class represents the header definitions of all user-added parameters in the trace format
 *
 * This explicitly implements LinkedHashMap to ensure that the data is retrieved in the same order as it was added
 */
public class TraceParameterDefinitions extends LinkedHashMap<String, TraceParameterDefinition<? extends TraceParameter>> {
    public int totalSize() {
        return values().stream().mapToInt(TraceParameterDefinition::getSize).sum();
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.flush();
        return baos.toByteArray();
    }

    public static TraceParameterDefinitions deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        TraceParameterDefinitions result;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            ObjectInputStream ois = new ObjectInputStream(bais);
            result = (TraceParameterDefinitions) ois.readObject();
        }
        return result;
    }
}
