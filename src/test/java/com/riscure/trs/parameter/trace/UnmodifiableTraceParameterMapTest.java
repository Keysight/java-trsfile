package com.riscure.trs.parameter.trace;

import com.riscure.trs.parameter.primitive.ByteArrayParameter;
import com.riscure.trs.parameter.primitive.IntegerArrayParameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class UnmodifiableTraceParameterMapTest {
    private TraceParameterMap immutable;

    @BeforeEach
    public void setup() {
        TraceParameterMap mutable = new TraceParameterMap();
        mutable.put("FOO", 1);

        immutable = UnmodifiableTraceParameterMap.of(mutable);
    }

    /**
     * This test ensure that the underlying map of an unmodifiable map cannot change the map itself
     */
    @Test
    public void testUnmodifiable() {
        byte[] ba = {1, 2, 3, 4, 5};
        ByteArrayParameter bap = new ByteArrayParameter(ba);
        TraceParameterMap tpm = new TraceParameterMap();
        tpm.put("BA", bap);
        TraceParameterMap copy = UnmodifiableTraceParameterMap.of(tpm);
        ba[1] = 6;
        byte[] baCopy = (byte[]) copy.get("BA").getValue();
        Assertions.assertFalse(Arrays.equals(baCopy, ba), "Arrays should not be equal, but they are");
    }


    @Test
    public void put() {
        Exception e = assertThrows(
                UnsupportedOperationException.class,
                () -> immutable.put("BLA", 2)
        );

        String expectedMessage = "Unable to set parameter `BLA` to `[2]`: This trace set is in read mode and cannot be modified.";
        String actualMessage = e.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void remove() {
        Exception e = assertThrows(
                UnsupportedOperationException.class,
                () -> immutable.remove("FOO")
        );

        String expectedMessage = "Unable to remove parameter `FOO`: This trace set is in read mode and cannot be modified.";
        String actualMessage = e.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void putAll() {
        TraceParameterMap source = new TraceParameterMap();
        source.put("BEEP", 5);
        source.put("BOOP", 7);

        Exception e = assertThrows(
                UnsupportedOperationException.class,
                () -> immutable.putAll(source)
        );

        String expectedMessage = "Unable to add all of `{BEEP=[5], BOOP=[7]}` : This trace set is in read mode and cannot be modified.";
        String actualMessage = e.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void clear() {
        Exception e = assertThrows(
                UnsupportedOperationException.class,
                () -> immutable.clear()
        );

        String expectedMessage = "Unable to modify: This trace set is in read mode and cannot be modified.";
        String actualMessage = e.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    public void replaceAll() {
        Exception e = assertThrows(
                UnsupportedOperationException.class,
                () -> immutable.replaceAll(
                        (key, oldValue)
                                -> new IntegerArrayParameter(new int[]{1})
                )
        );

        String expectedMessage = "Unable to modify: This trace set is in read mode and cannot be modified.";
        String actualMessage = e.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void putIfAbsent() {
        Exception e = assertThrows(
                UnsupportedOperationException.class,
                () -> immutable.putIfAbsent("BLA", new IntegerArrayParameter(new int[]{-1}))
        );

        String expectedMessage = "Unable to set parameter `BLA` to `[-1]`: This trace set is in read mode and cannot be modified.";
        String actualMessage = e.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testRemove() {
        Exception e = assertThrows(
                UnsupportedOperationException.class,
                () -> immutable.remove("BLA", "MEH")
        );

        String expectedMessage = "Unable to remove parameter `BLA`: This trace set is in read mode and cannot be modified.";
        String actualMessage = e.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void replace() {
        Exception e = assertThrows(
                UnsupportedOperationException.class,
                () -> immutable.replace("FOO", new IntegerArrayParameter(new int[]{1}), new IntegerArrayParameter(new int[]{-1}))
        );

        String expectedMessage = "Unable to set parameter `FOO` to `[-1]`: This trace set is in read mode and cannot be modified.";
        String actualMessage = e.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testReplace() {
        Exception e = assertThrows(
                UnsupportedOperationException.class,
                () -> immutable.replace("FOO", new IntegerArrayParameter(new int[]{-1}))
        );

        String expectedMessage = "Unable to set parameter `FOO` to `[-1]`: This trace set is in read mode and cannot be modified.";
        String actualMessage = e.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void merge() {


        Exception e = assertThrows(
                UnsupportedOperationException.class,
                () -> immutable.merge(
                        "BLA",
                        new IntegerArrayParameter(new int[]{77}),
                        (traceParameter, traceParameter2) -> new IntegerArrayParameter(new int[]{55})
                )
        );

        String expectedMessage = "Unable to modify: This trace set is in read mode and cannot be modified.";
        String actualMessage = e.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}