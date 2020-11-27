package com.riscure.trs.parameter.traceset;

import com.riscure.trs.parameter.primitive.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents the header definitions of all user-added global parameters of the trace set
 *
 * This explicitly implements LinkedHashMap to ensure that the data is retrieved in the same order as it was added
 */
public class TraceSetParameters extends LinkedHashMap<String, TraceSetParameter> {

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        //Write NE
        dos.writeShort(size());
        for (Map.Entry<String, TraceSetParameter> entry : entrySet()) {
            byte[] nameBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
            //Write NL
            dos.writeShort(nameBytes.length);
            //Write N
            dos.write(nameBytes);
            //Write value
            entry.getValue().serialize(dos);
        }
        dos.flush();
        return baos.toByteArray();
    }

    public static TraceSetParameters deserialize(byte[] bytes) throws IOException {
        TraceSetParameters result = new TraceSetParameters();
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
                //Read value
                result.put(name, TraceSetParameter.deserialize(dis));
            }
        }
        return result;
    }

    public void put(String key, byte value) {
        put(key, new byte[]{value});
    }

    public void put(String key, byte[] value) {
        put(key, new TraceSetParameter(new ByteArrayParameter(value)));
    }

    public void put(String key, short value) {
        put(key, new short[]{value});
    }

    public void put(String key, short[] value) {
        put(key, new TraceSetParameter(new ShortArrayParameter(value)));
    }

    public void put(String key, int value) {
        put(key, new int[]{value});
    }

    public void put(String key, int[] value) {
        put(key, new TraceSetParameter(new IntArrayParameter(value)));
    }

    public void put(String key, float value) {
        put(key, new float[]{value});
    }

    public void put(String key, float[] value) {
        put(key, new TraceSetParameter(new FloatArrayParameter(value)));
    }

    public void put(String key, long value) {
        put(key, new long[]{value});
    }

    public void put(String key, long[] value) {
        put(key, new TraceSetParameter(new LongArrayParameter(value)));
    }

    public void put(String key, double value) {
        put(key, new double[]{value});
    }

    public void put(String key, double[] value) {
        put(key, new TraceSetParameter(new DoubleArrayParameter(value)));
    }

    public void put(String key, String value) {
        put(key, new TraceSetParameter(new StringParameter(value)));
    }
}
