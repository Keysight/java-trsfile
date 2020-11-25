package com.riscure.trs;

import com.riscure.trs.enums.TRSTag;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinitions;
import com.riscure.trs.parameter.traceset.TraceSetParameters;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class TRSMetaDataUtils {
    private static final String IGNORED_UNKNOWN_TAG = "ignored unknown metadata tag '%02X' while reading a TRS file\n";
    private static final String TAG_LENGTH_INVALID = "The length field following tag '%s' has value '%X', which is not between 0 and 0xffff";
    private static final String UNSUPPORTED_TAG_TYPE = "Unsupported tag type for tag '%s': %s";
    private static final String REWINDING_STREAM = "The output stream is not at the start of the file. Rewinding stream.";

    /**
     * Writes the provided TRS metadata to the stream.
     * @param fos the file output stream
     * @param metaData the metadata to write
     * @throws IOException if any write error occurs
     * @throws TRSFormatException if the metadata contains unsupported tags
     */
    public static void writeTRSMetaData(FileOutputStream fos, TRSMetaData metaData) throws IOException, TRSFormatException {
        if (fos.getChannel().position() != 0) {
            System.err.println(REWINDING_STREAM);
            fos.getChannel().position(0);
        }
        for (TRSTag tag : TRSTag.values()) {
            if (tag.equals(TRSTag.TRACE_BLOCK)) continue;                     //TRACE BLOCK should be the last write
            if (!tag.isRequired() && metaData.hasDefaultValue(tag)) continue; //ignore if default and not required
            fos.write(tag.getValue());
            if (tag.getType() == String.class) {
                String s = metaData.getString(tag);
                int len = s.length();
                writeLength(fos, len);
                fos.write(s.getBytes());
            } else if (tag.getType() == Float.class) {
                float f = metaData.getFloat(tag);
                writeLength(fos, tag.getLength());
                writeInt(fos, Float.floatToIntBits(f), tag.getLength());
            } else if (tag.getType() == Boolean.class) {
                int value = metaData.getBoolean(tag) ? 1 : 0;
                writeLength(fos, tag.getLength());
                writeInt(fos, value, tag.getLength());
            } else if (tag.getType() == Integer.class) {
                writeLength(fos, tag.getLength());
                writeInt(fos, metaData.getInt(tag), tag.getLength());
            } else if (tag.getType() == TraceSetParameters.class) {
                byte[] serialized = metaData.getTraceSetParameters().serialize();
                writeLength(fos, serialized.length);
                fos.write(serialized);
            } else if (tag.getType() == TraceParameterDefinitions.class) {
                byte[] serialized = metaData.getTraceParameterDefinitions().serialize();
                writeLength(fos, serialized.length);
                fos.write(serialized);
            } else {
                throw new TRSFormatException(String.format(UNSUPPORTED_TAG_TYPE, tag.getName(), tag.getType()));
            }
        }
        fos.write(TRSTag.TRACE_BLOCK.getValue());
        fos.write(TRSTag.TRACE_BLOCK.getLength());
    }

    private static void writeInt(FileOutputStream fos, int value, int length) throws IOException {
        for (int i = 0; i < length; i++)
            fos.write((byte) (value >> (i * 8)));
    }

    private static void writeLength(FileOutputStream fos, long length) throws IOException {
        if (length > 0x7F) {
            int lenlen = 1 + (int) (Math.log(length) / Math.log(256));
            fos.write((byte) (0x80 + lenlen));
            for (int i = 0; i < lenlen; i++)
                fos.write((byte) (length >> (i * 8)));
        } else
            fos.write((byte) length);
    }

    /**
     * Reads the meta data of a TRS file. The {@code ByteBuffer} is assumed to be positioned at the start of the file; A
     * {@code TRSFormatException} will probably be thrown otherwise, since it cannot be parsed.
     * 
     * @param buffer The buffer which wraps the TRS file (should be positioned at the first byte of the file)
     * @return the meta data of a TRS file
     * @throws TRSFormatException If either the file is corrupt or the reader is not positioned at the start of the file
     */
    public static TRSMetaData readTRSMetaData(ByteBuffer buffer) throws TRSFormatException {
        TRSMetaData trs = TRSMetaData.create();
        byte tag;

        //We keep on reading meta data until we hit tag TB=0x5f
        do {
            // read meta data items and put them in trs
            tag = buffer.get();
            int length = buffer.get();
            if ((length & 0x80) != 0) {
                int addlen = length & 0x7F;
                length = 0;
                for (int i = 0; i < addlen; i++) {
                    length |= (buffer.get() & 0xFF) << (i * 8);
                }
            }
            readAndStoreData(buffer, tag, length, trs);
        } while (tag != TRSTag.TRACE_BLOCK.getValue());
        return trs;
    }

    private static void readAndStoreData(ByteBuffer buffer, byte tag, int length, TRSMetaData trsMD)
            throws TRSFormatException {
        boolean hasValidLength = (0 <= length & length <= 0xffff);
        TRSTag trsTag;
        try {
            trsTag = TRSTag.fromValue(tag);
        } catch (TRSFormatException ex) {
            //unknown tag, but read it anyway
            buffer.position(buffer.position() + length);
            System.err.printf(IGNORED_UNKNOWN_TAG, tag);
            return;
        }
        if (!hasValidLength) {
            throw new TRSFormatException(String.format(TAG_LENGTH_INVALID, trsTag.name(), length));
        }

        if (trsTag == TRSTag.TRACE_BLOCK) return;       //If we read TRACE_BLOCK, we've reached the end of the metadata

        if (trsTag.getType() == String.class) {
            trsMD.put(trsTag, readString(buffer, length));
        } else if (trsTag.getType() == Float.class) {
            trsMD.put(trsTag, readFloat(buffer));
        } else if (trsTag.getType() == Boolean.class) {
            trsMD.put(trsTag, readBoolean(buffer));
        } else if (trsTag.getType() == Integer.class) {
            trsMD.put(trsTag, readInt(buffer, length));
        } else if (trsTag.getType() == TraceSetParameters.class) {
            trsMD.put(trsTag, readTraceSetParameters(buffer, length));
        } else if (trsTag.getType() == TraceParameterDefinitions.class) {
            trsMD.put(trsTag, readTraceParameterDefinitions(buffer, length));
        } else {
            throw new TRSFormatException(String.format(UNSUPPORTED_TAG_TYPE, trsTag.getName(), trsTag.getType()));
        }
    }

    private static boolean readBoolean(ByteBuffer buffer) {
        return (buffer.get() & 0xFF) > 0;
    }

    private static int readInt(ByteBuffer buffer, int length) {
        long result = 0;
        for (int i = 0; i < length; i++) {
            result += (((int) buffer.get()) & 0xFF) << (8 * i);
        }
        return (int) result;
    }

    // Always reads 4 (four) bytes
    private static float readFloat(ByteBuffer buffer) {
        int intValue = readInt(buffer, 4);
        return Float.intBitsToFloat(intValue);
    }

    private static String readString(ByteBuffer buffer, int length) {
        byte[] ba = new byte[length];
        buffer.get(ba);

        return new String(ba);
    }

    private static TraceSetParameters readTraceSetParameters(ByteBuffer buffer, int length) throws TRSFormatException {
        byte[] ba = new byte[length];
        buffer.get(ba);

        try {
            return TraceSetParameters.deserialize(ba);
        } catch (IOException e) {
            throw new TRSFormatException(e);
        }
    }

    private static TraceParameterDefinitions readTraceParameterDefinitions(ByteBuffer buffer, int length) throws TRSFormatException {
        byte[] ba = new byte[length];
        buffer.get(ba);

        try {
            return TraceParameterDefinitions.deserialize(ba);
        } catch (IOException e) {
            throw new TRSFormatException(e);
        }
    }
}
