package com.riscure.trs;

import com.riscure.trs.enums.Encoding;
import com.riscure.trs.parameter.trace.TraceParameterMap;

import java.math.BigInteger;
import java.nio.FloatBuffer;

/**
 * Trace contains the data related to one consecutive array of samples,
 * including potential associated data and a title
 */
public class Trace {
    private static final String TO_STRING_FORMAT = "Trace{Title='%s', numberOfSamples=%d, shifted=%d, " +
            "aggregatesValid=%b, hasIllegalValues=%b, isReal=%b, max=%f, min=%f%n%s}";

    /**
     * Factory method. This will copy the sample array for stability.
     * @param sample the sample array
     * @return a new trace object holding the provided samples
     */
    public static Trace create(float[] sample) {
        return new Trace(sample.clone());
    }

    /**
     * Factory method. This will copy the provided arrays for stability.
     * @param title the trace title
     * @param sample the sample array
     * @param parameters the parameters to be saved with every trace
     * @return a new trace object holding the provided information
     */
    public static Trace create(String title, float[] sample, TraceParameterMap parameters) {
        return new Trace(title, sample.clone(), parameters);
    }

    /**
     * Creates a new instance of Trace which contains only a sample array
     * Do not modify the sample array, it may be used in the core!
     * 
     * @param sample Sample values. Do not modify
     */
    public Trace(float[] sample) {
        this.sample = FloatBuffer.wrap(sample);
    }

    /**
     * Creates a new instance of Trace containing title, (crypto) data and sample array
     * Do not modify the sample array, it may be used in the core!
     *
     * @param title Local title for this trace
     * @param sample Sample values. Do not modify
     * @param parameters the parameters to be saved with every trace. For backwards compatibility,
     *                   these values are also stored as a raw byte array
     */
    public Trace(String title, float[] sample, TraceParameterMap parameters) {
        this.title = title;
        this.sample = FloatBuffer.wrap(sample);
        this.parameters = parameters;
    }

    /**
     * Get the sample array, no shift corrections are done.
     * @return the sample array
     */
    public float[] getSample() {
        return sample.array();
    }

    /**
     * Force float sample coding
     */
    public void forceFloatCoding() {
        isReal = true;
    }

    /**
     * Get the preferred data type to store samples
     * 
     * @return the preferred data type to store samples
     **/
    public int getPreferredCoding() {
        if (!aggregatesValid) {
            updateAggregates();
        }

        Encoding preferredCoding;
        if (hasIllegalValues) {
            preferredCoding = Encoding.ILLEGAL;
        } else if (isReal) {
            preferredCoding = Encoding.FLOAT;
        } else if (max > Short.MAX_VALUE || min < Short.MIN_VALUE) {
            preferredCoding = Encoding.INT;
        } else if (max > Byte.MAX_VALUE || min < Byte.MIN_VALUE) {
            preferredCoding = Encoding.SHORT;
        } else {
            preferredCoding = Encoding.BYTE;
        }

        return preferredCoding.getValue();
    }

    private void updateAggregates() {
        for (float f : getSample()) {
            max = Math.max(f, max);
            min = Math.min(f, min);
            isReal |= f != (int) f;
            hasIllegalValues |= Float.isNaN(f) || Float.isInfinite(f) || f == Float.POSITIVE_INFINITY;
        }

        aggregatesValid = true;
    }

    /**
     * Get the trace title
     * 
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title for this trace.
     * 
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the supplementary (crypto) data of this trace.
     *
     * @return the supplementary data of this trace
     */
    public byte[] getData() {
        return parameters.toByteArray();
    }

    /**
     * Get the parameters of this trace
     *
     * @return the parameters of this trace
     */
    public TraceParameterMap getParameters() {
        return parameters;
    }

    /**
     * Get the supplementary (crypto) data of this trace as a hexadecimal string.
     *
     * @return the supplementary (crypto) data of this trace as a hexadecimal string
     */
    public String getDataString() {
        return new BigInteger(getData()).toString(16);
    }

    /**
     * Get the number of samples that this trace is shifted.
     *
     * @return the number of samples that this trace is shifted
     */
    public int getShifted() {
        return shifted;
    }

    /**
     * Set the number of samples that this trace is shifted
     * 
     * @param shifted number of shifted samples
     */
    public void setShifted(int shifted) {
        this.shifted = shifted;
    }

    /**
     * Get the length of the sample array.
     *
     * @return the length of the sample array
     */
    public int getNumberOfSamples() {
        return sample.limit();
    }

    /**
     * @return the trace set containing this trace, or null if not set
     */
    public TraceSet getTraceSet() {
        return traceSet;
    }

    /**
     * @param traceSet the trace set containing this trace
     */
    public void setTraceSet(TraceSet traceSet) {
        this.traceSet = traceSet;
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_FORMAT, title, sample.array().length, shifted,
                aggregatesValid, hasIllegalValues, isReal, max, min, parameters);
    }

    /** A map of all custom named trace parameters */
    private TraceParameterMap parameters = new TraceParameterMap();
    /** list of samples */
    private final FloatBuffer sample;
    /** trace title */
    private String title = null;
    /** number of samples shifted */
    private int shifted = 0;
    /** trace set including this trace */
    private TraceSet traceSet = null;
    /** Indicates whether the aggregates ({@link Trace#hasIllegalValues}, {@link Trace#isReal} {@link Trace#max}, {@link Trace#min}) are valid. */
    private boolean aggregatesValid = false;
    /** whether the trace contains illegal float values */
    private boolean hasIllegalValues = false;
    /** whether the trace contains real values */
    private boolean isReal = false;
    /** maximal value used in trace */
    private float max = 0;
    /** minimal value used in trace */
    private float min = 0;
}
