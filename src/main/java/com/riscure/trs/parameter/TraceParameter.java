package com.riscure.trs.parameter;

/**
 * This interface indicates that a class represents a parameter that is saved in <emphasis>every trace</emphasis> of
 * a trace set.
 */
public interface TraceParameter {
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
