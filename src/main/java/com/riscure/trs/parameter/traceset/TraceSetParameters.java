package com.riscure.trs.parameter.traceset;

import java.io.*;
import java.util.LinkedHashMap;

public class TraceSetParameters extends LinkedHashMap<String, Serializable> {
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.flush();
        return baos.toByteArray();
    }

    public static TraceSetParameters deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        TraceSetParameters result;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            ObjectInputStream ois = new ObjectInputStream(bais);
            result = (TraceSetParameters) ois.readObject();
        }
        return result;
    }
}
