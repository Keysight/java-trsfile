package com.riscure.trs;

import com.riscure.trs.enums.Encoding;
import com.riscure.trs.parameter.trace.TraceParameterMap;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinitionMap;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

import static com.riscure.trs.enums.TRSTag.*;
import static com.riscure.trs.enums.TRSTag.TRS_VERSION;

public class ReadOnlyTraceSet extends TraceSet {
    private static final String TRACE_SET_IN_READ_MODE = "TraceSet is in read mode. Please open the TraceSet in write mode.";
    private static final String ERROR_READING_FILE = "Error reading TRS file: file size (%d) != meta data (%d) + trace size (%d) * nr of traces (%d)";
    private static final String TRACE_INDEX_OUT_OF_BOUNDS = "Requested trace index (%d) is larger than the total number of available traces (%d).";
    private static final String UNKNOWN_SAMPLE_CODING = "Error reading TRS file: unknown sample coding '%d'";
    // This is excessive for the header, but it's only the initial maximum
    private static final long MAX_METADATA_SIZE = 100_000_000L;

    private final int metaDataSize;
    private final FileInputStream readStream;
    private final LargePreMappedFile mappedFile;
    private final float[] preallocatedSampleArray;
    private final TRSMetaData metaData;
    private final long fileSize;          //the total number of bytes in the underlying file

    private ByteBuffer metaDataBuffer;
    private byte[] preallocatedByteArray;
    private short[] preallocatedShortArray;
    private int[] preallocatedIntArray;

    ReadOnlyTraceSet(String inputFileName) throws IOException, TRSFormatException {
        super(Paths.get(inputFileName));

        this.readStream = new FileInputStream(inputFileName);
        FileChannel channel = readStream.getChannel();

        //the file might be bigger than the buffer, in which case we partially buffer it in memory
        this.fileSize = channel.size();
        long initialBufferSize = Math.min(fileSize, MAX_METADATA_SIZE);

        this.metaDataBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, initialBufferSize);
        this.metaData = TRSMetaDataUtils.readTRSMetaData(metaDataBuffer);
        this.metaDataSize = metaDataBuffer.position();
        this.metaDataBuffer.limit(metaDataSize);

        long traceSize = calculateTraceSize();
        this.mappedFile = new LargePreMappedFile(channel, metaDataSize, traceSize);

        int numberOfSamples = metaData.getInt(NUMBER_OF_SAMPLES);
        this.preallocatedSampleArray = new float[numberOfSamples];
    }

    /**
     * Get a trace from the set at the specified index
     * @param index the index of the Trace to read from the file
     * @return the Trace at the requested trace index
     * @throws IOException if a read error occurs
     * @throws IllegalArgumentException if this TraceSet is not ready be read from
     */
    @Override
    public Trace get(int index) throws IOException {
        if (!isOpen()) throw new IllegalArgumentException(TRACE_SET_NOT_OPEN);

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

        ByteBuffer buffer = mappedFile.getBuffer(index);

        String traceTitle = this.readTraceTitle(buffer);
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
                byte[] data = readData(buffer);
                traceParameterMap = new TraceParameterMap();
                if (data.length > 0) {
                    traceParameterMap.put("LEGACY_DATA", data);
                }
            }

            float[] samples = readSamples(buffer);
            // Since we are using an internal sample array in this class, Trace.create() should duplicate it internally
            return Trace.create(traceTitle, samples, traceParameterMap);
        } catch (TRSFormatException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void add(Trace trace) throws IOException, TRSFormatException {
        throw new IllegalArgumentException(TRACE_SET_IN_READ_MODE);
    }

    private long calculateTraceSize() {
        int sampleSize = Encoding.fromValue(metaData.getInt(SAMPLE_CODING)).getSize();
        long sampleSpace = metaData.getInt(NUMBER_OF_SAMPLES) * (long) sampleSize;
        return sampleSpace + metaData.getInt(DATA_LENGTH) + metaData.getInt(TITLE_SPACE);
    }

    @Override
    public void close() throws IOException, TRSFormatException {
        super.close();
        closeReader();
    }

    @Override
    public TRSMetaData getMetaData() {
        return metaData;
    }

    private void closeReader() throws IOException {
        metaDataBuffer = null;
        mappedFile.close();
        readStream.close();
    }

    protected String readTraceTitle(ByteBuffer buffer) {
        byte[] titleArray = new byte[metaData.getInt(TITLE_SPACE)];
        buffer.get(titleArray);
        return new String(titleArray);
    }

    protected byte[] readData(ByteBuffer buffer) {
        int inputSize = metaData.getInt(DATA_LENGTH);
        byte[] comDataArray = new byte[inputSize];
        buffer.get(comDataArray);
        return comDataArray;
    }

    /*
     * We can reuse the buffers when not dealing with float samples. They are instantiated once just in time if needed.
     */
    protected float[] readSamples(ByteBuffer buffer) throws TRSFormatException {
        switch (Encoding.fromValue(metaData.getInt(SAMPLE_CODING))) {
            case BYTE:
                this.preallocatedByteArray = this.preallocatedByteArray == null ? new byte[preallocatedSampleArray.length] : this.preallocatedByteArray;
                buffer.get(preallocatedByteArray);
                // Manual copy of byte[] into float[]
                for (int k = 0; k < preallocatedSampleArray.length; k++) {
                    preallocatedSampleArray[k] = preallocatedByteArray[k];
                }
                break;
            case SHORT:
                this.preallocatedShortArray = this.preallocatedShortArray == null ? new short[preallocatedSampleArray.length] : this.preallocatedShortArray;
                ShortBuffer shortView = buffer.asShortBuffer();
                shortView.get(preallocatedShortArray);
                // Manual copy of short[] into float[]
                for (int k = 0; k < preallocatedSampleArray.length; k++) {
                    preallocatedSampleArray[k] = preallocatedShortArray[k];
                }
                break;
            case FLOAT:
                FloatBuffer floatView = buffer.asFloatBuffer();
                floatView.get(preallocatedSampleArray);
                break;
            case INT:
                this.preallocatedIntArray = this.preallocatedIntArray == null ? new int[preallocatedSampleArray.length] : this.preallocatedIntArray;
                IntBuffer intView = buffer.asIntBuffer();
                intView.get(preallocatedIntArray);
                // Manual copy of int[] into float[]
                for (int k = 0; k < preallocatedIntArray.length; k++) {
                    preallocatedSampleArray[k] = (float) preallocatedIntArray[k];
                }
                break;
            default:
                throw new TRSFormatException(String.format(UNKNOWN_SAMPLE_CODING, metaData.getInt(SAMPLE_CODING)));
        }

        return preallocatedSampleArray;
    }
}
