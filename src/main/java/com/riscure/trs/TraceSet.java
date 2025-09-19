package com.riscure.trs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.riscure.trs.enums.TRSTag.TRS_VERSION;

public abstract class TraceSet implements AutoCloseable {
    protected static final String TRACE_SET_NOT_OPEN = "TraceSet has not been opened or has been closed.";
    // We want to pre-allocate 1M for the header, so we can grow it if needed without re-writing the whole file
    public static final long DEFAULT_METADATA_SIZE = 1_000_000L;

    //Shared variables
    private final Path path;
    private boolean open;

    protected TraceSet(Path path) {
        this.path = path;
        this.open = true;
    }

    /**
     * @return the Path on disk of this trace set
     */
    public Path getPath() {
        return path;
    }

    /**
     * @return whether this trace set is currently open
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Get a trace from the set at the specified index
     * @param index the index of the Trace to read from the file
     * @return the Trace at the requested trace index
     * @throws IOException if a read error occurs
     * @throws IllegalArgumentException if this TraceSet is not ready be read from
     */
    public abstract Trace get(int index) throws IOException;

    /**
     * Add a trace to a writable TraceSet
     * @param trace the Trace object to add
     * @throws IOException if any write error occurs
     * @throws TRSFormatException if the formatting of the trace is invalid
     */
    public abstract void add(Trace trace) throws IOException, TRSFormatException;

    @Override
    public void close() throws IOException, TRSFormatException {
        open = false;
    }

    /**
     * Get the metadata associated with this trace set
     * @return the metadata associated with this trace set
     */
    public abstract TRSMetaData getMetaData();

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
        return new ReadOnlyTraceSet(file);
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
        return new WritableTraceSet(file, metaData);
    }
}
