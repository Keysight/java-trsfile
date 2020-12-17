package com.riscure.trs.parameter.trace.definition;

import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.trace.TraceParameterMap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents the header definitions of all user-added parameters in the trace format
 *
 * This explicitly implements LinkedHashMap to ensure that the data is retrieved in the same order as it was added
 */
public class TraceParameterDefinitionMap extends LinkedHashMap<String, TraceParameterDefinition<TraceParameter>> {
    public int totalSize() {
        return values().stream().mapToInt(definition -> definition.getLength() * definition.getType().getByteSize()).sum();
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        //Write NE
        dos.writeShort(size());
        for (Map.Entry<String, TraceParameterDefinition<TraceParameter>> entry : entrySet()) {
            byte[] nameBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
            //Write NL
            dos.writeShort(nameBytes.length);
            //Write N
            dos.write(nameBytes);
            TraceParameterDefinition<? extends TraceParameter> value = entry.getValue();
            value.serialize(dos);
        }
        dos.flush();
        return baos.toByteArray();
    }

    public static TraceParameterDefinitionMap deserialize(byte[] bytes) throws IOException {
        TraceParameterDefinitionMap result = new TraceParameterDefinitionMap();
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
                //Read definition
                result.put(name, TraceParameterDefinition.deserialize(dis));
            }
        }
        return result;
    }

    /**
     * Create a set of definitions based on the parameters present in a trace.
     * @param parameters the parameters of the trace
     * @return a set of definitions based on the parameters present in a trace
     */
    public static TraceParameterDefinitionMap createFrom(TraceParameterMap parameters) {
        TraceParameterDefinitionMap definitions = new TraceParameterDefinitionMap();
        if (!parameters.isEmpty()) {
            short offset = 0;
            for (Map.Entry<String, TraceParameter> entry : parameters.entrySet()) {
                definitions.put(entry.getKey(), new TraceParameterDefinition<>(entry.getValue(), offset));
                offset += entry.getValue().length() * entry.getValue().getType().getByteSize();
            }
        }
        return definitions;
    }
}
