import com.riscure.trs.TRSFormatException;
import com.riscure.trs.TRSMetaData;
import com.riscure.trs.Trace;
import com.riscure.trs.TraceSet;
import com.riscure.trs.enums.Encoding;
import com.riscure.trs.enums.TRSTag;
import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.trace.TraceParameters;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinitions;
import com.riscure.trs.parameter.traceset.TraceSetParameters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestTraceSet {
    private static Path tempDir;
    private static final String BYTES_TRS = "bytes.trs";
    private static final String SHORTS_TRS = "shorts.trs";
    private static final String INTS_TRS = "ints.trs";
    private static final String FLOATS_TRS = "floats.trs";
    private static final String TRS = ".trs";
    private static final int NUMBER_OF_TRACES = 1024;
    private static final float[] BYTE_SAMPLES = new float[]{1, 2, 3, 4, 5};
    private static final float[] SHORT_SAMPLES = new float[]{1, 2, 3, 4, Byte.MAX_VALUE+1};
    private static final float[] INT_SAMPLES = new float[]{1, 2, 3, 4, Short.MAX_VALUE+1};
    private static final float[] FLOAT_SAMPLES = new float[]{1, 2, 3, 4, 5.1f};
    private static final String TVLA_STRING_VALUE = "Trace set contains the following TVLA sets: Random, R5S-Box_Out\n" +
            "AES-128 ENCRYPT (Input -> Output) Round 5 S-Box Out:HW(3~7)";

    @BeforeClass
    public static void createTempDir() throws IOException, TRSFormatException {
        tempDir = Files.createTempDirectory("TestTraceSet");

        try (TraceSet writable = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + BYTES_TRS)) {
            for (int k = 0; k < NUMBER_OF_TRACES; k++) {
                writable.add(Trace.create(BYTE_SAMPLES));
            }
        }

        try (TraceSet writable = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + SHORTS_TRS)) {
            for (int k = 0; k < NUMBER_OF_TRACES; k++) {
                writable.add(Trace.create(SHORT_SAMPLES));
            }
        }

        try (TraceSet writable = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + INTS_TRS)) {
            for (int k = 0; k < NUMBER_OF_TRACES; k++) {
                writable.add(Trace.create(INT_SAMPLES));
            }
        }

        try (TraceSet writable = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + FLOATS_TRS)) {
            for (int k = 0; k < NUMBER_OF_TRACES; k++) {
                writable.add(Trace.create(FLOAT_SAMPLES));
            }
        }
    }

    @AfterClass
    public static void cleanup() throws InterruptedException {
        //We need to allow a little time for java to release all handles
        System.gc();
        Thread.sleep(100);
        for (File file : Objects.requireNonNull(tempDir.toFile().listFiles())) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                System.err.printf("Failed to delete temporary file '%s'%n", file.toPath().toAbsolutePath().toString());
                e.printStackTrace();
            }
        }
        try {
            Files.delete(tempDir);
        } catch (IOException e) {
            System.err.printf("Failed to delete temporary folder '%s'%n", tempDir.toFile().toPath().toAbsolutePath().toString());
            e.printStackTrace();
        }
    }

    @Test
    public void testOpenBytes() throws IOException, TRSFormatException {
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + BYTES_TRS)) {
            int numberOfTracesRead = readable.getMetaData().getInt(TRSTag.NUMBER_OF_TRACES);
            Encoding encoding = Encoding.fromValue(readable.getMetaData().getInt(TRSTag.SAMPLE_CODING));
            assertEquals(Encoding.BYTE, encoding);
            assertEquals(NUMBER_OF_TRACES, numberOfTracesRead);
            for (int k = 0; k < NUMBER_OF_TRACES; k++) {
                Trace t = readable.get(k);
                assertEquals(Encoding.BYTE.getValue(), t.getPreferredCoding());
                assertArrayEquals(BYTE_SAMPLES, readable.get(k).getSample(), 0.01f);
            }
        }
    }

    @Test
    public void testOpenShorts() throws IOException, TRSFormatException {
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + SHORTS_TRS)) {
            int numberOfTracesRead = readable.getMetaData().getInt(TRSTag.NUMBER_OF_TRACES);
            Encoding encoding = Encoding.fromValue(readable.getMetaData().getInt(TRSTag.SAMPLE_CODING));
            assertEquals(Encoding.SHORT, encoding);
            assertEquals(NUMBER_OF_TRACES, numberOfTracesRead);
            for (int k = 0; k < NUMBER_OF_TRACES; k++) {
                Trace t = readable.get(k);
                assertEquals(Encoding.SHORT.getValue(), t.getPreferredCoding());
                assertArrayEquals(SHORT_SAMPLES, readable.get(k).getSample(), 0.01f);
            }
        }
    }

    @Test
    public void testOpenInts() throws IOException, TRSFormatException {
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + INTS_TRS)) {
            int numberOfTracesRead = readable.getMetaData().getInt(TRSTag.NUMBER_OF_TRACES);
            Encoding encoding = Encoding.fromValue(readable.getMetaData().getInt(TRSTag.SAMPLE_CODING));
            assertEquals(Encoding.INT, encoding);
            assertEquals(NUMBER_OF_TRACES, numberOfTracesRead);
            for (int k = 0; k < NUMBER_OF_TRACES; k++) {
                Trace t = readable.get(k);
                assertEquals(Encoding.INT.getValue(), t.getPreferredCoding());
                assertArrayEquals(INT_SAMPLES, readable.get(k).getSample(), 0.01f);
            }
        }
    }

    @Test
    public void testOpenFloats() throws IOException, TRSFormatException {
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + FLOATS_TRS)) {
            int numberOfTracesRead = readable.getMetaData().getInt(TRSTag.NUMBER_OF_TRACES);
            Encoding encoding = Encoding.fromValue(readable.getMetaData().getInt(TRSTag.SAMPLE_CODING));
            assertEquals(Encoding.FLOAT, encoding);
            assertEquals(NUMBER_OF_TRACES, numberOfTracesRead);
            for (int k = 0; k < NUMBER_OF_TRACES; k++) {
                Trace t = readable.get(k);
                assertEquals(Encoding.FLOAT.getValue(), t.getPreferredCoding());
                assertArrayEquals(FLOAT_SAMPLES, readable.get(k).getSample(), 0.01f);
            }
        }
    }

    /**
     * This tests adding several different types of information to the trace set header. The three parameters are chosen
     * to match the three major cases: Strings, primitives, and arbitrary (serializable) objects.
     *
     * @throws IOException
     * @throws TRSFormatException
     */
    @Test
    public void testWriteTraceSetParameters() throws IOException, TRSFormatException {
        TRSMetaData metaData = new TRSMetaData();
        metaData.put(TRSTag.TRS_VERSION, 2);
        TraceSetParameters parameters = new TraceSetParameters();
        parameters.put("BYTE", (byte)1);
        parameters.put("SHORT", (short)2);
        parameters.put("INT", 3);
        parameters.put("FLOAT", (float)4);
        parameters.put("LONG", (long)5);
        parameters.put("DOUBLE", (double)6);
        parameters.put("STRING", String.format("%3d", 7));
        parameters.put("BYTEARRAY", new byte[]{(byte) 8, (byte) 9, (byte) 0});
        parameters.put("SHORTARRAY", new short[]{(short) 1, (short) 2, (short) 3});
        parameters.put("INTARRAY", new int[]{4, 5, 6});
        parameters.put("FLOATARRAY", new float[]{(float) 7, (float) 8, (float) 9});
        parameters.put("LONGARRAY", new long[]{0, 1, 2});
        parameters.put("DOUBLEARRAY", new double[]{3, 4, 5});
        parameters.put("TVLA", TVLA_STRING_VALUE);
        //parameters.put("XYZ offset", XYZ_TEST_VALUE);
        metaData.put(TRSTag.TRACE_SET_PARAMETERS, parameters);
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData).close();
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            TraceSetParameters readTraceSetParameters = readable.getMetaData().getTraceSetParameters();
            parameters.forEach((s, traceSetParameter) -> {
                assertEquals(traceSetParameter, readTraceSetParameters.get(s));
            });
        }
    }

    /**
     * This tests adding a parameter with a name of 100000 characters
     *
     * @throws IOException
     * @throws TRSFormatException
     */
    @Test(expected = TRSFormatException.class)
    public void testWriteTraceParametersInvalidName() throws IOException, TRSFormatException {
        TRSMetaData metaData = new TRSMetaData();
        String parameterName = String.format("%100000s", "XYZ");
        metaData.put(TRSTag.TRS_VERSION, 2);
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData)) {
            TraceParameters parameters = new TraceParameters();
            parameters.put(parameterName, 1);
            traceWithParameters.add(Trace.create("", FLOAT_SAMPLES, parameters));
        }
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            TraceParameterDefinitions parameterDefinitions = readable.getMetaData().getTraceParameterDefinitions();
            parameterDefinitions.forEach((key, parameter) -> assertEquals(parameterName, key));
        }
    }

    /**
     * This tests whether any primitives, Strings, byte arrays, and Serializables can be added to a set of parameters
     *
     * @throws IOException
     * @throws TRSFormatException
     */
    @Test
    public void testWritePrimitiveTraceParametersNoDefinitions() throws IOException, TRSFormatException {
        TRSMetaData metaData = new TRSMetaData();
        metaData.put(TRSTag.TRS_VERSION, 2);
        List<TraceParameters> testParameters = new ArrayList<>();
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData)) {
            for (int k = 0; k < 25; k++) {
                TraceParameters parameters = new TraceParameters();
                parameters.put("BYTE", (byte)k);
                parameters.put("SHORT", (short)k);
                parameters.put("INT", k);
                parameters.put("FLOAT", (float)k);
                parameters.put("LONG", (long)k);
                parameters.put("DOUBLE", (double)k);
                parameters.put("STRING", String.format("%3d", k));
                parameters.put("BYTEARRAY", new byte[]{(byte) k, (byte) k, (byte) k});
                parameters.put("SHORTARRAY", new short[]{(short) k, (short) k, (short) k});
                parameters.put("INTARRAY", new int[]{k, k, k});
                parameters.put("FLOATARRAY", new float[]{(float) k, (float) k, (float) k});
                parameters.put("LONGARRAY", new long[]{k, k, k});
                parameters.put("DOUBLEARRAY", new double[]{k, k, k});
                //parameters.put("SERIALIZABLE", new XYZDefaultTestData(k % 5, k / 5, k));
                traceWithParameters.add(Trace.create("", FLOAT_SAMPLES, parameters));
                testParameters.add(parameters);
            }
        }
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            TraceParameterDefinitions parameterDefinitions = readable.getMetaData().getTraceParameterDefinitions();
            for (int k = 0; k < 25; k++) {
                assertEquals(parameterDefinitions.size(), testParameters.get(k).size());
                Trace trace = readable.get(k);
                TraceParameters correctValue = testParameters.get(k);
                parameterDefinitions.forEach((key, parameter) -> {
                    TraceParameter traceParameter = trace.getParameters().get(key);
                    assertEquals(traceParameter, correctValue.get(key));
                });
            }
        }
    }

    /**
     * This tests whether any strings added to the trace are handled as specified:
     * - if no length is specified, the first string is leading
     * - if a string is longer than the length specified, it should be truncated
     * - when truncated, a string should still be valid UTF-8 (truncated at character level, not byte level)
     *
     * @throws IOException
     * @throws TRSFormatException
     */
    @Test
    public void testWriteTraceParametersVaryingStringLength() throws IOException, TRSFormatException {
        TRSMetaData metaData = new TRSMetaData();
        metaData.put(TRSTag.TRS_VERSION, 2);
        List<TraceParameters> testParameters = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        strings.add("abcd");
        strings.add("abcdefgh");
        strings.add("ab");
        strings.add("abcdefgh汉字");
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData)) {
            for (int k = 0; k < 25; k++) {
                TraceParameters parameters = new TraceParameters();
                parameters.put("BYTEARRAY", new byte[]{(byte) k, (byte) k, (byte) k});
                parameters.put(TraceParameter.SAMPLES, new float[]{(float) k, (float) k, (float) k});
                parameters.put("name", strings.get(k % strings.size()));
                //parameters.put("SERIALIZABLE", new XYZDefaultTestData(k % 5, k / 5, k));
                traceWithParameters.add(Trace.create("", FLOAT_SAMPLES, parameters));
                testParameters.add(parameters);
            }
        }
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            TraceParameterDefinitions parameterDefinitions = readable.getMetaData().getTraceParameterDefinitions();
            for (int k = 0; k < 25; k++) {
                assertEquals(parameterDefinitions.size(), testParameters.get(k).size());
                Trace trace = readable.get(k);
                TraceParameters correctValue = testParameters.get(k);
                parameterDefinitions.forEach((key, parameter) -> {
                    TraceParameter traceParameter = trace.getParameters().get(key);
                    assertEquals(traceParameter, correctValue.get(key));
                });
            }
        }
    }
}
