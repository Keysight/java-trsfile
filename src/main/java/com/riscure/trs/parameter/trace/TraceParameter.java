package com.riscure.trs.parameter.trace;

import java.io.IOException;
import java.io.Serializable;

public interface TraceParameter extends Serializable {
    /**
     * Returns a serialized version of this parameter
     * @return the value converted to a byte array
     */
    byte[] serialize() throws IOException;

    /**
     * Fill this parameter with the values read back from the trace
     * @param bytes the byte array to read the values from
     */
    void deserialize(byte[] bytes) throws IOException, ClassNotFoundException;
}
