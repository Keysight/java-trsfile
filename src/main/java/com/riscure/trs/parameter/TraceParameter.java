package com.riscure.trs.parameter;

/**
 * This interface represents a parameter that is used in the trace data or the trace set header
 */
public interface TraceParameter {
    String SAMPLES = "SAMPLES";
    String TITLE = "TITLE";
    String INPUT = "INPUT";
    String OUTPUT = "OUTPUT";
    String KEY = "KEY";

    /**
     * The number of values of this type in this parameter
     * @return the number of values of this type in this parameter
     */
    int length();

    /**
     * @return The type of the parameter.
     */
    ParameterType getType();

    /**
     * @return The value of the parameter.
     */
    Object getValue();
}
