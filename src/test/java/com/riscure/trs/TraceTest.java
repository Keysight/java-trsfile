package com.riscure.trs;

import com.riscure.trs.parameter.trace.TraceParameterMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TraceTest {

    @Test
    void getDataStringReturnsUnsignedHexadecimal() {
        assertDataString(new byte[] {(byte) 0xFF, (byte) 0x80, 0x01}, "FF8001");
    }

    @Test
    void getDataStringPreservesLeadingZeroBytes() {
        assertDataString(new byte[] {0x00, 0x00, 0x16}, "000016");
    }

    @Test
    void getDataStringDoesNotPrefixHighBitValues() {
        assertDataString(new byte[] {(byte) 0xFF, (byte) 0xFF, 0x55}, "FFFF55");
    }

    private static void assertDataString(byte[] data, String expected) {
        TraceParameterMap parameters = new TraceParameterMap();
        parameters.put("data", data);
        Trace trace = Trace.create("title", new float[] {0}, parameters);

        String actual = trace.getDataString();
        assertEquals(expected, actual);
        assertEquals(data.length * 2, actual.length());
    }
}
