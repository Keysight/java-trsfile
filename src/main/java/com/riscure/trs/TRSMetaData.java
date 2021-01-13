package com.riscure.trs;

import com.riscure.trs.enums.TRSTag;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinitionMap;
import com.riscure.trs.parameter.trace.definition.UnmodifiableTraceParameterDefinitionMap;
import com.riscure.trs.parameter.traceset.TraceSetParameterMap;
import com.riscure.trs.parameter.traceset.UnmodifiableTraceSetParameterMap;

import java.util.HashMap;
import java.util.Map;

public class TRSMetaData {
    private static final String IGNORING_NEW_VALUE = "%s: Ignoring new value (%s) because previously defined value is non-default (%s) and overwrite is disabled.%n";
    private static final String INCOMPATIBLE_TYPES = "Failed to add tag %s: Expected type (%s) does not match actual type (%s).";

    private final Map<TRSTag, Object> metaData;

    /**
     * Creates a TRS metadata object with all default values
     */
    public TRSMetaData() {
        metaData = new HashMap<>();
        init();
    }

    private void init() {
        for (TRSTag tag : TRSTag.values()) {
            metaData.put(tag, tag.getDefaultValue());
        }
    }

    /**
     * Add the data associated with the supplied tag to this metadata.
     * This will overwrite any existing value
     * @param tag the tag for which to save the metadata
     * @param data the data to save
     */
    public void put(TRSTag tag, Object data) {
        put(tag, data, true);
    }

    /**
     * Add the data associated with the supplied tag to this metadata
     * @param tag the tag for which to save the metadata
     * @param data the data to save
     * @param overwriteNonDefault whether to overwrite non-default values currently saved for this tag
     */
    public void put(TRSTag tag, Object data, boolean overwriteNonDefault) {
        if (!tag.getType().isAssignableFrom(data.getClass())) {
            throw new IllegalArgumentException(
                    String.format(INCOMPATIBLE_TYPES, tag.name(), tag.getType(), data.getClass()));
        }

        if (hasDefaultValue(tag) || overwriteNonDefault) {
            metaData.put(tag, data);
        } else {
            System.err.printf(IGNORING_NEW_VALUE, tag.name(), data.toString(), metaData.get(tag));
        }
    }

    /**
     * Get the value of the associated tag
     * @param tag the tag to get the data for
     * @return the value of the supplied tag in the metadata. The type of the data is {@link TRSTag#getType()}.
     */
    public Object get(TRSTag tag) {
        return metaData.get(tag);
    }

    /**
     * Get the value of the associated tag as an int. The type of the data can be found by calling {@link TRSTag#getType()}.
     * @param tag the tag to get the data for
     * @return the Integer value of the supplied tag in the metadata
     * @throws ClassCastException if the type does not match the type of the TRSTag
     */
    public int getInt(TRSTag tag) {
        return (Integer) metaData.get(tag);
    }

    /**
     * Get the value of the associated tag as a String. The type of the data can be found by calling {@link TRSTag#getType()}.
     * @param tag the tag to get the data for
     * @return the String value of the supplied tag in the metadata
     * @throws ClassCastException if the type does not match the type of the TRSTag
     */
    public String getString(TRSTag tag) {
        return (String) metaData.get(tag);
    }

    /**
     * Get the value of the associated tag as a boolean. The type of the data can be found by calling {@link TRSTag#getType()}.
     * @param tag the tag to get the data for
     * @return the Boolean value of the supplied tag in the metadata
     * @throws ClassCastException if the type does not match the type of the TRSTag
     */
    public boolean getBoolean(TRSTag tag) {
        return (Boolean) metaData.get(tag);
    }

    /**
     * Get the value of the associated tag as a float. The type of the data can be found by calling {@link TRSTag#getType()}.
     * @param tag the tag to get the data for
     * @return the Float value of the supplied tag in the metadata
     * @throws ClassCastException if the type does not match the type of the TRSTag
     */
    public float getFloat(TRSTag tag) {
        return (Float) metaData.get(tag);
    }

    /**
     * Get the TraceSetParameters of this trace set, or an empty set if they are undefined
     * @return the TraceSetParameters of this trace set
     */
    public TraceSetParameterMap getTraceSetParameters() {
        Object o = metaData.get(TRSTag.TRACE_SET_PARAMETERS);
        if (TraceSetParameterMap.class.isAssignableFrom(o.getClass())) {
            return (TraceSetParameterMap) o;
        }
        return UnmodifiableTraceSetParameterMap.of(new TraceSetParameterMap());
    }

    /**
     * Get the TraceParameterDefinitions of this trace set, or an empty set if they are undefined
     * @return the TraceParameterDefinitions of this trace set
     */
    public TraceParameterDefinitionMap getTraceParameterDefinitions() {
        Object o = metaData.get(TRSTag.TRACE_PARAMETER_DEFINITIONS);
        if (TraceParameterDefinitionMap.class.isAssignableFrom(o.getClass())) {
            return (TraceParameterDefinitionMap) o;
        }
        return UnmodifiableTraceParameterDefinitionMap.of(new TraceParameterDefinitionMap());
    }

    /**
     * Check whether the supplied tag has the default value in this metadata
     * @param tag the tag to check the data for
     * @return true if the value of the supplied tag is default, false otherwise
     */
    public boolean hasDefaultValue(TRSTag tag) {
        return tag.getDefaultValue().equals(metaData.get(tag));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (TRSTag tag: TRSTag.values()) {
            builder.append(tag)
                    .append("\n\tValue = ")
                    .append(get(tag))
                    .append("\n");
        }
        return builder.toString();
    }

    /**
     * Factory method for convenience
     * @return a new TRSMetaData instance with all default values
     */
    public static TRSMetaData create() {
        return new TRSMetaData();
    }
}
