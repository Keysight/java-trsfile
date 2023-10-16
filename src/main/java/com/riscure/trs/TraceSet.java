package com.riscure.trs;

import com.riscure.trs.enums.Encoding;
import com.riscure.trs.enums.ParameterType;
import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.StringParameter;
import com.riscure.trs.parameter.trace.TraceParameterMap;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinition;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinitionMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.riscure.trs.enums.TRSTag.*;

public class TraceSet implements AutoCloseable {
    private static final String ERROR_READING_FILE = "Error reading TRS file: file size (%d) != meta data (%d) + trace size (%d) * nr of traces (%d)";
    private static final String TRACE_SET_NOT_OPEN = "TraceSet has not been opened or has been closed.";
    private static final String TRACE_SET_IN_WRITE_MODE = "TraceSet is in write mode. Please open the TraceSet in read mode.";
    private static final String TRACE_INDEX_OUT_OF_BOUNDS = "Requested trace index (%d) is larger than the total number of available traces (%d).";
    private static final String TRACE_SET_IN_READ_MODE = "TraceSet is in read mode. Please open the TraceSet in write mode.";
    private static final String TRACE_LENGTH_DIFFERS = "All traces in a set need to be the same length, but current trace length (%d) differs from the previous trace(s) (%d)";
    private static final String TRACE_DATA_LENGTH_DIFFERS = "All traces in a set need to have the same data length, but current trace data length (%d) differs from the previous trace(s) (%d)";
    private static final String UNKNOWN_SAMPLE_CODING = "Error reading TRS file: unknown sample coding '%d'";
    private static final long MAX_BUFFER_SIZE = Integer.MAX_VALUE;
    private static final String PARAMETER_NOT_DEFINED = "Parameter %s is saved in the trace, but was not found in the header definition";
    private static final CharsetDecoder UTF8_DECODER = StandardCharsets.UTF_8.newDecoder();

    //Reading variables
    private int metaDataSize;
    private FileInputStream readStream;
    private FileChannel channel;

    private ByteBuffer buffer;

    private long bufferStart;       //the byte index of the file where the buffer window starts
    private long bufferSize;        //the number of bytes that are in the buffer window
    private long fileSize;          //the total number of bytes in the underlying file

    //Writing variables
    private FileOutputStream writeStream;

    private boolean firstTrace = true;

    //Shared variables
    private final TRSMetaData metaData;
    private boolean open;
    private final boolean writing;        //whether the trace is opened in write mode
    private final Path path;

    private TraceSet(String inputFileName) throws IOException, TRSFormatException {
        this.writing = false;
        this.open = true;
        this.path = Paths.get(inputFileName);
        this.readStream = new FileInputStream(inputFileName);
        this.channel = readStream.getChannel();

        //the file might be bigger than the buffer, in which case we partially buffer it in memory
        this.fileSize = this.channel.size();
        this.bufferStart = 0L;
        this.bufferSize = Math.min(fileSize, MAX_BUFFER_SIZE);

        mapBuffer();
        this.metaData = TRSMetaDataUtils.readTRSMetaData(buffer);
        this.metaDataSize = buffer.position();
    }

    private TraceSet(String outputFileName, TRSMetaData metaData) throws FileNotFoundException {
        this.open = true;
        this.writing = true;
        this.metaData = metaData;
        this.path = Paths.get(outputFileName);
        this.writeStream = new FileOutputStream(outputFileName);
    }

    /**
     * @return the Path on disk of this trace set
     */
    public Path getPath() {
        return path;
    }

    private void mapBuffer() throws IOException {
        this.buffer = this.channel.map(FileChannel.MapMode.READ_ONLY, this.bufferStart, this.bufferSize);
    }

    private void moveBufferIfNecessary(int traceIndex) throws IOException {
        long traceSize = calculateTraceSize();
        long start = metaDataSize + (long) traceIndex * traceSize;
        long end = start + traceSize;

        boolean moveRequired = start < this.bufferStart || this.bufferStart + this.bufferSize < end;
        if (moveRequired) {
            this.bufferStart = start;
            this.bufferSize = Math.min(this.fileSize - start, MAX_BUFFER_SIZE);
            this.mapBuffer();
        }
    }

    private long calculateTraceSize() {
        int sampleSize = Encoding.fromValue(metaData.getInt(SAMPLE_CODING)).getSize();
        long sampleSpace = metaData.getInt(NUMBER_OF_SAMPLES) * (long) sampleSize;
        return sampleSpace + metaData.getInt(DATA_LENGTH) + metaData.getInt(TITLE_SPACE);
    }

    /**
     * Get a trace from the set at the specified index
     * @param index the index of the Trace to read from the file
     * @return the Trace at the requested trace index
     * @throws IOException if a read error occurs
     * @throws IllegalArgumentException if this TraceSet is not ready be read from
     */
    public Trace get(int index) throws IOException {
        if (!open) throw new IllegalArgumentException(TRACE_SET_NOT_OPEN);
        if (writing) throw new IllegalArgumentException(TRACE_SET_IN_WRITE_MODE);

        long traceSize = calculateTraceSize();
        long nrOfTraces = this.metaData.getInt(NUMBER_OF_TRACES);
        if (index >= nrOfTraces) {
            String msg = String.format(TRACE_INDEX_OUT_OF_BOUNDS, index, nrOfTraces);
            throw new IllegalArgumentException(msg);
        }

        long calculatedFileSize = metaDataSize + traceSize * nrOfTraces;
        if (fileSize != calculatedFileSize) {
            String msg = String.format(ERROR_READING_FILE, fileSize, metaDataSize, traceSize, nrOfTraces);
            throw new IllegalStateException(msg);
        }

        moveBufferIfNecessary(index);

        long absolutePosition = metaDataSize + index * traceSize;
        buffer.position((int) (absolutePosition - this.bufferStart));

        String traceTitle = this.readTraceTitle();
        if (traceTitle.trim().isEmpty()) {
            traceTitle = String.format("%s %d", metaData.getString(GLOBAL_TITLE), index);
        }

        try {
            TraceParameterMap traceParameterMap;
            if (metaData.getInt(TRS_VERSION) > 1) {
                TraceParameterDefinitionMap traceParameterDefinitionMap = metaData.getTraceParameterDefinitions();
                int size = traceParameterDefinitionMap.totalSize();
                byte[] data = new byte[size];
                buffer.get(data);
                traceParameterMap = TraceParameterMap.deserialize(data, traceParameterDefinitionMap);
            } else {
                //legacy mode
                byte[] data = readData();
                traceParameterMap = new TraceParameterMap();
                if (data.length > 0) {
                    traceParameterMap.put("LEGACY_DATA", data);
                }
            }

            float[] samples = readSamples();
            return new Trace(traceTitle, samples, traceParameterMap);
        } catch (TRSFormatException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Add a trace to a writable TraceSet
     * @param trace the Trace object to add
     * @throws IOException if any write error occurs
     * @throws TRSFormatException if the formatting of the trace is invalid
     */
    public void add(Trace trace) throws IOException, TRSFormatException {
        if (!open) throw new IllegalArgumentException(TRACE_SET_NOT_OPEN);
        if (!writing) throw new IllegalArgumentException(TRACE_SET_IN_READ_MODE);
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
        UTF8_DECODER.reset();
        UTF8_DECODER.onMalformedInput(CodingErrorAction.IGNORE);
        UTF8_DECODER.decode(bb, cb, true);
        UTF8_DECODER.flush(cb);
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

    @Override
    public void close() throws IOException, TRSFormatException {
        open = false;
        if (writing) closeWriter();
        else closeReader();
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

    private void closeReader() throws IOException {
        readStream.close();
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
     * Get the metadata associated with this trace set
     * @return the metadata associated with this trace set
     */
    public TRSMetaData getMetaData() {
        return metaData;
    }

    protected String readTraceTitle() {
        byte[] titleArray = new byte[metaData.getInt(TITLE_SPACE)];
        buffer.get(titleArray);
        return new String(titleArray);
    }

    protected byte[] readData() {
        int inputSize = metaData.getInt(DATA_LENGTH);
        byte[] comDataArray = new byte[inputSize];
        buffer.get(comDataArray);
        return comDataArray;
    }

    protected float[] readSamples() throws TRSFormatException {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int numberOfSamples = metaData.getInt(NUMBER_OF_SAMPLES);
        float[] samples;
        switch (Encoding.fromValue(metaData.getInt(SAMPLE_CODING))) {
            case BYTE:
                byte[] byteData = new byte[numberOfSamples];
                buffer.get(byteData);
                samples = toFloatArray(byteData);
                break;
            case SHORT:
                ShortBuffer shortView = buffer.asShortBuffer();
                short[] shortData = new short[numberOfSamples];
                shortView.get(shortData);
                samples = toFloatArray(shortData);
                break;
            case FLOAT:
                FloatBuffer floatView = buffer.asFloatBuffer();
                samples = new float[numberOfSamples];
                floatView.get(samples);
                break;
            case INT:
                IntBuffer intView = buffer.asIntBuffer();
                int[] intData = new int[numberOfSamples];
                intView.get(intData);
                samples = toFloatArray(intData);
                break;
            default:
                throw new TRSFormatException(String.format(UNKNOWN_SAMPLE_CODING, metaData.getInt(SAMPLE_CODING)));
        }

        return samples;
    }

    private float[] toFloatArray(byte[] numbers) {
        float[] result = new float[numbers.length];
        for (int k = 0; k < numbers.length; k++) {
            result[k] = numbers[k];
        }
        return result;
    }

    private float[] toFloatArray(int[] numbers) {
        float[] result = new float[numbers.length];
        for (int k = 0; k < numbers.length; k++) {
            result[k] = (float) numbers[k];
        }
        return result;
    }

    private float[] toFloatArray(short[] numbers) {
        float[] result = new float[numbers.length];
        for (int k = 0; k < numbers.length; k++) {
            result[k] = numbers[k];
        }
        return result;
    }

    /**
     * Factory method. This creates a new open TraceSet for reading.
     * The resulting TraceSet is a live view on the file, and loads from the file directly.
     * Remember to close the TraceSet when done.
     * @param file the path to the TRS file to open
     * @return the TraceSet representation of the file
     * @throws IOException when any read exception is encountered
     * @throws TRSFormatException when any incorrect formatting of the TRS file is encountered
     */
    public static TraceSet open(String file) throws IOException, TRSFormatException {
        return new TraceSet(file);
    }

    /**
     * A one-shot creator of a TRS file. The metadata not related to the trace list is assumed to be default.
     * @param file the path to the file to save
     * @param traces the list of traces to save in the file
     * @throws IOException when any write exception is encountered
     * @throws TRSFormatException when any TRS formatting issues arise from saving the provided traces
     */
    public static void save(String file, List<Trace> traces) throws IOException, TRSFormatException {
        TRSMetaData trsMetaData = TRSMetaData.create();
        save(file, traces, trsMetaData);
    }

    /**
     * A one-shot creator of a TRS file. Any unfilled fields of metadata are assumed to be default.
     * @param file the path to the file to save
     * @param traces the list of traces to save in the file
     * @param metaData the metadata associated with the set to create
     * @throws IOException when any write exception is encountered
     * @throws TRSFormatException when any TRS formatting issues arise from saving the provided traces
     */
    public static void save(String file, List<Trace> traces, TRSMetaData metaData) throws IOException, TRSFormatException {
        TraceSet traceSet = create(file, metaData);
        for (Trace trace : traces) {
            traceSet.add(trace);
        }
        traceSet.close();
    }

    /**
     * Create a new traceset file at the specified location. <br>
     * NOTE: The metadata is fully defined by the first added trace. <br>
     * Every next trace is expected to adhere to the following parameters: <br>
     * NUMBER_OF_SAMPLES is equal to the number of samples in the first trace <br>
     * DATA_LENGTH is equal to the binary data size of the first trace <br>
     * TITLE_SPACE is defined by the length of the first trace title (including spaces) <br>
     * SCALE_X is defined for the whole set based on the sampling frequency of the first trace <br>
     * SAMPLE_CODING is defined for the whole set based on the values of the first trace <br>
     * @param file the path to the file to be created
     * @return a writable trace set object
     * @throws IOException if the file creation failed
     */
    public static TraceSet create(String file) throws IOException {
        TRSMetaData trsMetaData = TRSMetaData.create();
        return create(file, trsMetaData);
    }

    /**
     * Create a new traceset file at the specified location. <br>
     * NOTE: The supplied metadata is leading, and is not overwritten.
     * Please make sure that the supplied values are correct <br>
     * Every next trace is expected to adhere to the following parameters: <br>
     * NUMBER_OF_SAMPLES is equal to the number of samples in the first trace <br>
     * DATA_LENGTH is equal to the binary data size of the first trace <br>
     * TITLE_SPACE is defined by the length of the first trace title (including spaces) <br>
     * SCALE_X is defined for the whole set based on the sampling frequency of the first trace <br>
     * SAMPLE_CODING is defined for the whole set based on the values of the first trace <br>
     * @param file the path to the file to be created
     * @param metaData the user-supplied meta data
     * @return a writable trace set object
     * @throws IOException if the file creation failed
     */
    public static TraceSet create(String file, TRSMetaData metaData) throws IOException {
        metaData.put(TRS_VERSION, 2, false);
        return new TraceSet(file, metaData);
    }
}
