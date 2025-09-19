package com.riscure.trs;

import com.riscure.trs.enums.Encoding;
import com.riscure.trs.enums.ParameterType;
import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.StringParameter;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinition;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinitionMap;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import static com.riscure.trs.enums.TRSTag.*;

public class WritableTraceSet extends TraceSet {
    private static final String TRACE_SET_IN_WRITE_MODE = "TraceSet is in write mode. Please open the TraceSet in read mode.";
    private static final String TRACE_LENGTH_DIFFERS = "All traces in a set need to be the same length, but current trace length (%d) differs from the previous trace(s) (%d)";
    private static final String TRACE_DATA_LENGTH_DIFFERS = "All traces in a set need to have the same data length, but current trace data length (%d) differs from the previous trace(s) (%d)";
    private static final String PARAMETER_NOT_DEFINED = "Parameter %s is saved in the trace, but was not found in the header definition";

    private final CharsetDecoder utf8Decoder = StandardCharsets.UTF_8.newDecoder();
    private final TRSMetaData metaData;
    private final FileOutputStream writeStream;

    private boolean firstTrace = true;

    WritableTraceSet(String outputFileName, TRSMetaData metaData) throws FileNotFoundException {
        super(Paths.get(outputFileName));
        this.metaData = metaData;
        this.writeStream = new FileOutputStream(outputFileName);
    }

    @Override
    public Trace get(int index) throws IOException {
        throw new IllegalArgumentException(TRACE_SET_IN_WRITE_MODE);
    }

    @Override
    public void add(Trace trace) throws IOException, TRSFormatException {
        if (!isOpen()) throw new IllegalArgumentException(TRACE_SET_NOT_OPEN);
        if (firstTrace) {
            int dataLength = trace.getData() == null ? 0 : trace.getData().length;
            int titleLength = trace.getTitle() == null ? 0 : trace.getTitle().getBytes(StandardCharsets.UTF_8).length;
            metaData.put(NUMBER_OF_SAMPLES, trace.getNumberOfSamples(), false);
            metaData.put(DATA_LENGTH, dataLength, false);
            metaData.put(TITLE_SPACE, titleLength, false);
            metaData.put(SAMPLE_CODING, trace.getPreferredCoding(), false);
            metaData.put(TRACE_PARAMETER_DEFINITIONS, TraceParameterDefinitionMap.createFrom(trace.getParameters()));
            TRSMetaDataUtils.writeTRSMetaData(writeStream, metaData);
            firstTrace = false;
        }
        truncateStrings(trace, metaData);
        checkValid(trace);

        trace.setTraceSet(this);
        writeTrace(trace);

        int numberOfTraces = metaData.getInt(NUMBER_OF_TRACES);
        metaData.put(NUMBER_OF_TRACES, numberOfTraces + 1);
    }

    @Override
    public TRSMetaData getMetaData() {
        return metaData;
    }

    @Override
    public void close() throws IOException, TRSFormatException {
        super.close();
        closeWriter();
    }

    private void closeWriter() throws IOException, TRSFormatException {
        try {
            //reset writer to start of file and overwrite header
            writeStream.getChannel().position(0);
            TRSMetaDataUtils.writeTRSMetaData(writeStream, metaData);
            writeStream.flush();
        } finally {
            writeStream.close();
        }
    }

    /**
     * This method makes sure that the trace title and any added string parameters adhere to the preset maximum length
     * @param trace the trace to update
     * @param metaData the metadata specifying the maximum string lengths
     */
    private void truncateStrings(Trace trace, TRSMetaData metaData) {
        int titleSpace = metaData.getInt(TITLE_SPACE);
        trace.setTitle(fitUtf8StringToByteLength(trace.getTitle(), titleSpace));
        TraceParameterDefinitionMap traceParameterDefinitionMap = metaData.getTraceParameterDefinitions();
        for (Map.Entry<String, TraceParameterDefinition<TraceParameter>> definition : traceParameterDefinitionMap.entrySet()) {
            TraceParameterDefinition<TraceParameter> value = definition.getValue();
            String key = definition.getKey();
            if (value.getType() == ParameterType.STRING) {
                short stringLength = value.getLength();
                String stringValue = ((StringParameter) trace.getParameters().get(key)).getValue();
                if (stringLength != stringValue.getBytes(StandardCharsets.UTF_8).length) {
                    trace.getParameters().put(key, fitUtf8StringToByteLength(stringValue, stringLength));
                }
            }
        }
    }

    /**
     * Fits a string to the number of characters that fit in X bytes avoiding multi byte characters being cut in
     * half at the cut off point. Also handles surrogate pairs where 2 characters in the string is actually one literal
     * character. If the string is too long, it is truncated. If it's too short, it's padded with NUL characters.
     * @param s the string to fit
     * @param maxBytes the number of bytes required
     */
    private String fitUtf8StringToByteLength(String s, int maxBytes) {
        if (s == null) {
            return null;
        }
        byte[] sba = s.getBytes(StandardCharsets.UTF_8);
        if (sba.length <= maxBytes) {
            return new String(Arrays.copyOf(sba, maxBytes));
        }
        // Ensure truncation by having byte buffer = maxBytes
        ByteBuffer bb = ByteBuffer.wrap(sba, 0, maxBytes);
        CharBuffer cb = CharBuffer.allocate(maxBytes);
        // Ignore an incomplete character
        utf8Decoder.reset();
        utf8Decoder.onMalformedInput(CodingErrorAction.IGNORE);
        utf8Decoder.decode(bb, cb, true);
        utf8Decoder.flush(cb);
        return new String(cb.array(), 0, cb.position());
    }

    private void writeTrace(Trace trace) throws TRSFormatException, IOException {
        String title = trace.getTitle() == null ? "" : trace.getTitle();
        writeStream.write(title.getBytes(StandardCharsets.UTF_8));
        byte[] data = trace.getData() == null ? new byte[0] : trace.getData();
        writeStream.write(data);
        Encoding encoding = Encoding.fromValue(metaData.getInt(SAMPLE_CODING));
        writeStream.write(toByteArray(trace.getSample(), encoding));
    }

    private byte[] toByteArray(float[] samples, Encoding encoding) throws TRSFormatException {
        byte[] result;
        switch (encoding) {
            case ILLEGAL:
                throw new TRSFormatException("Illegal sample encoding");
            case BYTE:
                result = new byte[samples.length];
                for (int k = 0; k < samples.length; k++) {
                    if (samples[k] != (byte)samples[k]) throw new IllegalArgumentException("Byte sample encoding too small");
                    result[k] = (byte) samples[k];
                }
                break;
            case SHORT:
                result = new byte[samples.length * 2];
                for (int k = 0; k < samples.length; k++) {
                    if (samples[k] != (short)samples[k]) throw new IllegalArgumentException("Short sample encoding too small");
                    short value = (short) samples[k];
                    result[2*k] = (byte) value;
                    result[2*k + 1] = (byte) (value >> 8);
                }
                break;
            case INT:
                result = new byte[samples.length * 4];
                for (int k = 0; k < samples.length; k++) {
                    int value = (int) samples[k];
                    result[4*k] = (byte) value;
                    result[4*k + 1] = (byte) (value >> 8);
                    result[4*k + 2] = (byte) (value >> 16);
                    result[4*k + 3] = (byte) (value >> 24);
                }
                break;
            case FLOAT:
                result = new byte[samples.length * 4];
                for (int k = 0; k < samples.length; k++) {
                    int value = Float.floatToIntBits(samples[k]);
                    result[4*k] = (byte) value;
                    result[4*k + 1] = (byte) (value >> 8);
                    result[4*k + 2] = (byte) (value >> 16);
                    result[4*k + 3] = (byte) (value >> 24);
                }
                break;
            default:
                throw new TRSFormatException(String.format("Sample encoding not supported: %s", encoding.name()));
        }
        return result;
    }

    private void checkValid(Trace trace) {
        int numberOfSamples = metaData.getInt(NUMBER_OF_SAMPLES);
        if (metaData.getInt(NUMBER_OF_SAMPLES) != trace.getNumberOfSamples()) {
            throw new IllegalArgumentException(String.format(TRACE_LENGTH_DIFFERS,
                    trace.getNumberOfSamples(),
                    numberOfSamples));
        }

        int dataLength = metaData.getInt(DATA_LENGTH);
        int traceDataLength = trace.getData() == null ? 0 : trace.getData().length;
        if (metaData.getInt(DATA_LENGTH) != traceDataLength) {
            throw new IllegalArgumentException(String.format(TRACE_DATA_LENGTH_DIFFERS,
                    traceDataLength,
                    dataLength));
        }

        for (Map.Entry<String, TraceParameter> entry : trace.getParameters().entrySet()) {
            if (!metaData.getTraceParameterDefinitions().containsKey(entry.getKey())) {
                throw new IllegalArgumentException(String.format(PARAMETER_NOT_DEFINED, entry.getKey()));
            }
        }
    }
}
