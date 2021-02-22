package com.riscure.trs.parameter.trace;

import com.riscure.trs.types.ByteArrayTypeKey;
import com.riscure.trs.types.TypedKey;

import static org.junit.jupiter.api.Assertions.*;

class TraceParameterMapTest {

    @org.junit.jupiter.api.Test
    void modifyingOriginalAfterMakingACopyShouldNotChangeCopy() {
        TraceParameterMap original = new TraceParameterMap();
        byte[] ba = new byte[]{0, 1, 2, 3};
        ByteArrayTypeKey key = new ByteArrayTypeKey("a byte array");
        original.put(key, ba);

        TraceParameterMap copyOfOriginal = new TraceParameterMap(original); // copy original

        ba[0] = -1; // modify original (after creating a copy)

        byte[] actual = copyOfOriginal.get(key);
        byte[] expected = new byte[]{0, 1, 2, 3};
        assertArrayEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void modifyingCopyShouldNotChangeOriginal() {
        TraceParameterMap original = new TraceParameterMap();
        byte[] ba = new byte[]{0, 1, 2, 3};
        ByteArrayTypeKey key = new ByteArrayTypeKey("a byte array");
        original.put(key, ba);

        TraceParameterMap copyOfOriginal = new TraceParameterMap(original); // copy original
        copyOfOriginal.get(key)[0] = -1; // modify copy
        assertArrayEquals(new byte[]{-1, 1, 2, 3}, copyOfOriginal.get(key)); // check that array in copy is changed

        byte[] actual = original.get(key);
        byte[] expected = new byte[]{0, 1, 2, 3};
        assertArrayEquals(expected, actual);
    }
}
