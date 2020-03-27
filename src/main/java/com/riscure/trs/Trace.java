package com.riscure.trs;

import com.riscure.trs.enums.Encoding;

import java.math.BigInteger;
import java.nio.FloatBuffer;

/**
 * Trace contains the data related to one consecutive array of samples,
 * including potential associated data and a title
 */
public class Trace {
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
     * @param data the communication data array
     * @param sample the sample array
     * @return a new trace object holding the provided samples
     */
    public static Trace create(String title, byte[] data, float[] sample) {
        return new Trace(title, data.clone(), sample.clone());
    }

    /**
     * Factory method. This will copy the provided arrays for stability.
     * @param title the trace title
     * @param data the communication data array
     * @param sample the sample array
     * @param sampleFrequency the associated sample frequency
     * @return a new trace object holding the provided samples
     */
    public static Trace create(String title, byte[] data, float[] sample, float sampleFrequency) {
        return new Trace(title, data.clone(), sample.clone(), sampleFrequency);
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
     * @param title
     *            Local title for this trace
     * @param data
     *            Supplementary (crypto) data
     * @param sample
     *            Sample values. Do not modify
     */
    public Trace(String title, byte[] data, float[] sample) {
        this.title = title;
        this.data = data;
        this.sample = FloatBuffer.wrap(sample);
    }

    /**
     * Creates a new instance of Trace containing title, (crypto) data and sample array
     * Do not modify the sample array, it may be used in the core!
     *
     * @param title
     *            Local title for this trace
     * @param data
     *            Supplementary (crypto) data
     * @param sample
     *            Sample values. Do not modify
     * @param sampleFrequency
     *            Sampling frequency at which the samples were acquired.
     */
    public Trace(String title, byte[] data, float[] sample, float sampleFrequency) {
        this.title = title;
        this.data = data;
        this.sample = FloatBuffer.wrap(sample);
        this.sampleFrequency = sampleFrequency;
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
     * @return data type
     **/
    public int getPreferredCoding() {
        if (!aggregatesValid)
            updateAggregates();

        if (hasIllegalValues)
            return Encoding.ILLEGAL.getValue();
        if (isReal)
            return Encoding.FLOAT.getValue();
        if (max > Short.MAX_VALUE || min < Short.MIN_VALUE)
            return Encoding.INT.getValue();
        if (max > Byte.MAX_VALUE || min < Byte.MIN_VALUE)
            return Encoding.SHORT.getValue();
        return Encoding.BYTE.getValue();
    }

    private void updateAggregates() {
        float[] sample = getSample();

        // Update aggregates
        for (float f : sample) {
            if (f > max)
                max = f;
            if (f < min)
                min = f;
            if (f != (int) f)
                isReal = true;
            if (Float.isNaN(f) || Float.isInfinite(f) || f == Float.POSITIVE_INFINITY)
                hasIllegalValues = true;
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
     * @param title
     *            the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the trace sample frequency
     * 
     * @return The sample frequency
     */
    public float getSampleFrequency() {
        return sampleFrequency;
    }

    /**
     * Set the sample frequency for this trace.
     * 
     * @param sampleFrequency
     *            the sample frequency
     */
    public void setSampleFrequency(float sampleFrequency) {
        this.sampleFrequency = sampleFrequency;
    }

    /**
     * Get the supplementary (crypto) data of this trace.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Set the supplementary (crypto) data for this trace.
     * 
     * @param data the new (crypto) data
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * Get the supplementary (crypto) data of this trace as a hexadecimal string.
     */
    public String getDataString() {
        return new BigInteger(data).toString(16);
    }

    /**
     * Get the number of samples that this trace is shifted.
     */
    public int getShifted() {
        return shifted;
    }

    /**
     * Set the number of samples that this trace is shifted
     * 
     * @param shifted
     *            number of shifted samples
     */
    public void setShifted(int shifted) {
        this.shifted = shifted;
    }

    /**
     * Get the length of the sample array.
     */
    public int getNumberOfSamples() {
        return sample.limit();
    }

    /**
     * Get the TraceSet containing this Trace.
     */
    public TraceSet getTraceSet() {
        return ts;
    }

    /**
     * set the TraceSet for this Trace
     * 
     * @param ts
     *            the new TraceSet
     */
    public void setTraceSet(TraceSet ts) {
        this.ts = ts;
    }

    private FloatBuffer sample;
    /** trace title */
    public String title = null;
    /** trace (crypto) data */
    public byte[] data = null;
    /** number of samples shifted */
    public int shifted = 0;
    /** trace set including this trace */
    public TraceSet ts = null;
    /** sample frequency of this trace */
    public float sampleFrequency = 1;
    /**
     * Indicates whether the aggregates are valid: hasIllegalValues, isReal,
     * max, min
     */
    private boolean aggregatesValid = false;
    /** whether the trace contains illegal float values */
    private boolean hasIllegalValues = false;
    /** whether the trace contains real values */
    private boolean isReal = false;
    /** maximal value used in trace */
    private float max = 0, min = 0;
}
