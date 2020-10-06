package com.riscure.trs.parameter.trace;

import java.io.Serializable;

/**
 * This interface indicates that a class represents a parameter that is saved in <emphasis>every trace</emphasis> of
 * a trace set. This assumes the following contract:
 * - The class must have a public 0-argument constructor
 * - The class must have a getter and setter for every contained value
 * - Every value in the class must be serializable
 *
 * As the serialized version of this parameter is stored in every trace, it is recommended to have {@link #serialize()}
 * only return the bare minimum length required to represent the object contents.
 */
public interface TraceParameter extends Serializable {
    /**
     * Creates a serialized version of this parameter. The length needs to be the same for every trace in a trace set.
     * @return the value converted to a byte array
     */
    byte[] serialize();

    /**
     * Fill this parameter with the values read back from the provided byte array
     * @param bytes the byte array to read the values from
     */
    void deserialize(byte[] bytes);

    /**
     * The length of the serialized parameter.
     * @return the length of the serialized parameter
     */
    int length();
}
