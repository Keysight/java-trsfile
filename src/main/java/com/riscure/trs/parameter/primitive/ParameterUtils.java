package com.riscure.trs.parameter.primitive;

import com.riscure.trs.parameter.ParameterType;
import com.riscure.trs.parameter.TraceParameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ParameterUtils {
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.UTF_8);

    public static TraceParameter deserialize(ParameterType type, short length, DataInputStream dis) throws IOException {
        switch (type) {
            case BYTE:
                if (length > 1) return ByteArrayParameter.deserialize(dis, length);
                return ByteParameter.deserialize(dis);
            case SHORT:
                if (length > 1) return ShortArrayParameter.deserialize(dis, length);
                return ShortParameter.deserialize(dis);
            case INT:
                if (length > 1) return IntArrayParameter.deserialize(dis, length);
                return IntParameter.deserialize(dis);
            case FLOAT:
                if (length > 1) return FloatArrayParameter.deserialize(dis, length);
                return FloatParameter.deserialize(dis);
            case LONG:
                if (length > 1) return LongArrayParameter.deserialize(dis, length);
                return LongParameter.deserialize(dis);
            case DOUBLE:
                if (length > 1) return DoubleArrayParameter.deserialize(dis, length);
                return DoubleParameter.deserialize(dis);
            case STRING:
                return StringParameter.deserialize(dis, length);
            default:
                throw new IllegalArgumentException("Unknown parameter type: " + type.name());
        }
    }

    public static void serialize(TraceParameter parameter, DataOutputStream dos) throws IOException {
        int length = parameter.length();
        switch (parameter.getType()) {
            case BYTE:
                if (length > 1) ((ByteArrayParameter) parameter).serialize(dos);
                else ((ByteParameter) parameter).serialize(dos);
                break;
            case SHORT:
                if (length > 1) ((ShortArrayParameter) parameter).serialize(dos);
                else ((ShortParameter) parameter).serialize(dos);
                break;
            case INT:
                if (length > 1) ((IntArrayParameter) parameter).serialize(dos);
                else ((IntParameter) parameter).serialize(dos);
                break;
            case FLOAT:
                if (length > 1) ((FloatArrayParameter) parameter).serialize(dos);
                else ((FloatParameter) parameter).serialize(dos);
                break;
            case LONG:
                if (length > 1) ((LongArrayParameter) parameter).serialize(dos);
                else ((LongParameter) parameter).serialize(dos);
                break;
            case DOUBLE:
                if (length > 1) ((DoubleArrayParameter) parameter).serialize(dos);
                else ((DoubleParameter) parameter).serialize(dos);
                break;
            case STRING:
                ((StringParameter) parameter).serialize(dos);
                break;
            default:
                throw new IllegalArgumentException("Unknown parameter type: " + parameter.getType().name());
        }
    }

    public static String toHexString(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}
