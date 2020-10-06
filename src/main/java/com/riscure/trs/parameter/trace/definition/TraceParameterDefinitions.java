package com.riscure.trs.parameter.trace.definition;

import com.riscure.trs.parameter.trace.TraceParameter;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TraceParameterDefinitions {
    private final Map<String, TraceParameterDefinition<? extends TraceParameter>> parameters;

    /**
     * Create a new set of {@link TraceParameterDefinitions}. In order for a trace parameter to be saved and
     * loaded correctly, it is required to be added to the definitions using the same name.
     */
    public TraceParameterDefinitions() {
        parameters = new LinkedHashMap<>();
    }

    public void put(String key, TraceParameterDefinition<? extends TraceParameter> parameterTemplate) {
        parameters.put(key, parameterTemplate);
    }

    public Map<String, TraceParameterDefinition<? extends TraceParameter>> getParameters() {
        return parameters;
    }

    public int getSize() {
        return parameters.values().stream().mapToInt(TraceParameterDefinition::getLength).sum();
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        for (Map.Entry<String, TraceParameterDefinition<? extends TraceParameter>> entry : parameters.entrySet()) {
            String name = entry.getKey();
            oos.writeObject(name);
            oos.writeObject(entry.getValue());
        }
        oos.flush();
        return baos.toByteArray();
    }

    public static TraceParameterDefinitions deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        TraceParameterDefinitions result = new TraceParameterDefinitions();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            ObjectInputStream ois = new ObjectInputStream(bais);
            while (bais.available() > 0) {
                String name = (String) ois.readObject();
                result.put(name, (TraceParameterDefinition<? extends TraceParameter>) ois.readObject());
            }
        }
        return result;
    }
}
