package com.riscure.trs.parameter.trace;

import java.io.Serializable;

public interface TraceParameter extends Serializable {
    /**
     * Returns a serialized version of this parameter. The length needs to be the same for every trace in a trace set.
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
