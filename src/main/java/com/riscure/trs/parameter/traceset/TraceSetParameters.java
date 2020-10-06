package com.riscure.trs.parameter.traceset;

import com.riscure.trs.TRSFormatException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TraceSetParameters {
    private final Map<String, Serializable> parameters;

    public TraceSetParameters() {
        parameters = new LinkedHashMap<>();
    }

    public void put(String key, Serializable parameter) {
        parameters.put(key, parameter);
    }

    public Map<String, Serializable> getParameters() {
        return parameters;
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        for (Map.Entry<String, Serializable> entry : parameters.entrySet()) {
            String name = entry.getKey();
            oos.writeObject(name);
            oos.writeObject(entry.getValue());
        }
        oos.flush();
        return baos.toByteArray();
    }

    public static TraceSetParameters deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        TraceSetParameters result = new TraceSetParameters();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            ObjectInputStream ois = new ObjectInputStream(bais);
            while (bais.available() > 0) {
                String name = (String) ois.readObject();
                Serializable object = (Serializable) ois.readObject();
                result.put(name, object);
            }
        }
        return result;
    }
}
