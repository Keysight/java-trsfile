package com.riscure.trs.enums;

import com.riscure.trs.TRSFormatException;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinitionMap;
import com.riscure.trs.parameter.traceset.TraceSetParameterMap;

public enum TRSTag {
    NUMBER_OF_TRACES                 (0x41, "NT", true,     Integer.class,      4,      0,                 "Number of traces"),
    NUMBER_OF_SAMPLES                (0x42, "NS", true,     Integer.class,      4,      0,                 "Number of samples per trace"),
    SAMPLE_CODING                    (0x43, "SC", true,     Integer.class,      1,      Encoding.FLOAT.getValue(),     "Sample Coding (see SampleCoding class),"),
    DATA_LENGTH                      (0x44, "DS", false,    Integer.class,      2,      0,                 "Length of cryptographic data included in trace"),
    TITLE_SPACE                      (0x45, "TS", false,    Integer.class,      1,      0,                 "Title space reserved per trace"),
    GLOBAL_TITLE                     (0x46, "GT", false,    String.class,       0,      "trace",           "Global trace title"),
    DESCRIPTION                      (0x47, "DC", false,    String.class,       0,      "",                "Description"),
    OFFSET_X                         (0x48, "XO", false,    Integer.class,      4,      0,                 "Offset in X-axis for trace representation"),
    LABEL_X                          (0x49, "XL", false,    String.class,       0,      "",                "Label of X-axis"),
    LABEL_Y                          (0x4A, "YL", false,    String.class,       0,      "",                "Label of Y-axis"),
    SCALE_X                          (0x4B, "XS", false,    Float.class,        4,      1f,                "Scale value for X-axis"),
    SCALE_Y                          (0x4C, "YS", false,    Float.class,        4,      1f,                "Scale value for Y-axis"),
    TRACE_OFFSET                     (0x4D, "TO", false,    Integer.class,      4,      0,                 "Trace offset for displaying trace numbers"),
    LOGARITHMIC_SCALE                (0x4E, "LS", false,    Boolean.class,      1,      false,             "Logarithmic scale"),
    TRS_VERSION                      (0x4F, "VS", false,    Integer.class,      1,      0,                 "The version of the traceset format"),
    ACQUISITION_RANGE_OF_SCOPE       (0x55, "RG", false,    Float.class,        4,      0f,                "Range of the scope used to perform acquisition"),
    ACQUISITION_COUPLING_OF_SCOPE    (0x56, "CL", false,    Integer.class,      4,      0,                 "Coupling of the scope used to perform acquisition"),
    ACQUISITION_OFFSET_OF_SCOPE      (0x57, "OS", false,    Float.class,        4,      0f,                "Offset of the scope used to perform acquisition"),
    ACQUISITION_INPUT_IMPEDANCE      (0x58, "II", false,    Float.class,        4,      0f,                "Input impedance of the scope used to perform acquisition"),
    ACQUISITION_DEVICE_ID            (0x59, "AI", false,    String.class,       0,      "",                "Device ID of the scope used to perform acquisition"),
    ACQUISITION_TYPE_FILTER          (0x5A, "FT", false,    Integer.class,      4,      0,                 "The type of filter used during acquisition"),
    ACQUISITION_FREQUENCY_FILTER     (0x5B, "FF", false,    Float.class,        4,      0f,                "Frequency of the filter used during acquisition"),
    ACQUISITION_RANGE_FILTER         (0x5C, "FR", false,    Float.class,        4,      0f,                "Range of the filter used during acquisition"),
    TRACE_BLOCK                      (0x5F, "TB", true,     Integer.class,      0,      0,                 "Trace block marker: an empty TLV that marks the end of the header"),
    EXTERNAL_CLOCK_USED              (0x60, "EU", false,    Boolean.class,      1,      false,             "External clock used"),
    EXTERNAL_CLOCK_THRESHOLD         (0x61, "ET", false,    Float.class,        4,      0f,                "External clock threshold"),
    EXTERNAL_CLOCK_MULTIPLIER        (0x62, "EM", false,    Integer.class,      4,      0,                 "External clock multiplier"),
    EXTERNAL_CLOCK_PHASE_SHIFT       (0x63, "EP", false,    Integer.class,      4,      0,                 "External clock phase shift"),
    EXTERNAL_CLOCK_RESAMPLER_MASK    (0x64, "ER", false,    Integer.class,      4,      0,                 "External clock resampler mask"),
    EXTERNAL_CLOCK_RESAMPLER_ENABLED (0x65, "RE", false,    Boolean.class,      1,      false,             "External clock resampler enabled"),
    EXTERNAL_CLOCK_FREQUENCY         (0x66, "EF", false,    Float.class,        4,      0f,                "External clock frequency"),
    EXTERNAL_CLOCK_BASE              (0x67, "EB", false,    Integer.class,      4,      0,                 "External clock time base"),
    NUMBER_VIEW                      (0x68, "VT", false,    Integer.class,      4,      0,                 "View number of traces: number of traces to show on opening"),
    TRACE_OVERLAP                    (0x69, "OV", false,    Boolean.class,      1,      false,             "Overlap: whether to overlap traces in case of multi trace view"),
    GO_LAST_TRACE                    (0x6A, "GL", false,    Boolean.class,      1,      false,             "Go to last trace on opening"),
    INPUT_OFFSET                     (0x6B, "IO", false,    Integer.class,      4,      0,                 "Input data offset in trace data"),
    OUTPUT_OFFSET                    (0x6C, "OO", false,    Integer.class,      4,      0,                 "Output data offset in trace data"),
    KEY_OFFSET                       (0x6D, "KO", false,    Integer.class,      4,      0,                 "Key data offset in trace data"),
    INPUT_LENGTH                     (0x6E, "IL", false,    Integer.class,      4,      0,                 "Input data length in trace data"),
    OUTPUT_LENGTH                    (0x6F, "OL", false,    Integer.class,      4,      0,                 "Output data length in trace data"),
    KEY_LENGTH                       (0x70, "KL", false,    Integer.class,      4,      0,                 "Key data length in trace data"),
    NUMBER_OF_ENABLED_CHANNELS       (0x71, "CH", false,    Integer.class,      4,      0,                 "Number of oscilloscope channels used for measurement"),
    NUMBER_OF_USED_OSCILLOSCOPES     (0x72, "NO", false,    Integer.class,      4,      0,                 "Number of oscilloscopes used for measurement"),
    XY_SCAN_WIDTH                    (0x73, "WI", false,    Integer.class,      4,      0,                 "Number of steps in the \"x\" direction during XY scan"),
    XY_SCAN_HEIGHT                   (0x74, "HE", false,    Integer.class,      4,      0,                 "Number of steps in the \"y\" direction during XY scan"),
    XY_MEASUREMENTS_PER_SPOT         (0x75, "ME", false,    Integer.class,      4,      0,                 "Number of consecutive measurements done per spot during XY scan"),
    TRACE_SET_PARAMETERS             (0x76, "GP", false,    TraceSetParameterMap.class, 0, 0,                 "The set of custom global trace set parameters"),
    TRACE_PARAMETER_DEFINITIONS      (0x77, "LP", false,    TraceParameterDefinitionMap.class, 0,    0,                 "The set of custom local trace parameters");

    private static final String UNKNOWN_TAG = "Unknown tag: 0x%X";

    private final byte value;
    private final String name;
    private final boolean required;
    private final Class<?> type;
    private final int length;
    private final Object defaultValue;
    private final String description;

    TRSTag(int value, String name, boolean required, Class<?> type, int length, Object defaultValue, String description) {
        this.value = (byte) value;
        this.name = name;
        this.required = required;
        this.type = type;
        this.length = length;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public byte getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public Class<?> getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public static TRSTag fromValue(byte value) throws TRSFormatException {
        for (TRSTag tag : TRSTag.values()) {
            if (tag.getValue() == value) return tag;
        }
        throw new TRSFormatException(String.format(UNKNOWN_TAG, value));
    }

    @Override
    public String toString() {
        return String.format("%s (value=%X, name=%s, %srequired, type=%s, length=%d, default=%s, description=%s)",
                name(),
                getValue(),
                getName(),
                isRequired() ? "" : "not ",
                getType().getName(),
                getLength(),
                getDefaultValue(),
                getDescription());
    }
}
