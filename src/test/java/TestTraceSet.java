import com.riscure.trs.TRSFormatException;
import com.riscure.trs.TRSMetaData;
import com.riscure.trs.Trace;
import com.riscure.trs.TraceSet;
import com.riscure.trs.enums.Encoding;
import com.riscure.trs.enums.TRSTag;
import com.riscure.trs.parameter.trace.TraceParameter;
import com.riscure.trs.parameter.trace.TraceParameters;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinition;
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

import static org.junit.Assert.*;

public class TestTraceSet {
    private static Path tempDir;
    private static final String BYTES_TRS = "bytes.trs";
    private static final String SHORTS_TRS = "shorts.trs";
    private static final String INTS_TRS = "ints.trs";
    private static final String FLOATS_TRS = "floats.trs";
    private static final String TRS = ".trs";
    private static final int NUMBER_OF_TRACES = 1024;
    private static final float[] byteSamples = new float[]{1, 2, 3, 4, 5};
    private static final float[] shortSamples = new float[]{1, 2, 3, 4, Byte.MAX_VALUE+1};
    private static final float[] intSamples = new float[]{1, 2, 3, 4, Short.MAX_VALUE+1};
    private static final float[] floatSamples = new float[]{1, 2, 3, 4, 5.1f};
    private static final String TVLA_STRING_VALUE = "Trace set contains the following TVLA sets: Random, R5S-Box_Out\n" +
            "AES-128 ENCRYPT (Input -> Output) Round 5 S-Box Out:HW(3~7)";
    private static final XYZTestData XYZ_TEST_VALUE = new XYZTestData(0, 1, 2);

    @BeforeClass
    public static void createTempDir() throws IOException, TRSFormatException {
        tempDir = Files.createTempDirectory("TestTraceSet");

        try (TraceSet writable = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + BYTES_TRS)) {
            for (int k = 0; k < NUMBER_OF_TRACES; k++) {
                writable.add(Trace.create(byteSamples));
            }
        }

        try (TraceSet writable = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + SHORTS_TRS)) {
            for (int k = 0; k < NUMBER_OF_TRACES; k++) {
                writable.add(Trace.create(shortSamples));
            }
        }

        try (TraceSet writable = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + INTS_TRS)) {
            for (int k = 0; k < NUMBER_OF_TRACES; k++) {
                writable.add(Trace.create(intSamples));
            }
        }

        try (TraceSet writable = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + FLOATS_TRS)) {
            for (int k = 0; k < NUMBER_OF_TRACES; k++) {
                writable.add(Trace.create(floatSamples));
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
                assertArrayEquals(byteSamples, readable.get(k).getSample(), 0.01f);
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
                assertArrayEquals(shortSamples, readable.get(k).getSample(), 0.01f);
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
                assertArrayEquals(intSamples, readable.get(k).getSample(), 0.01f);
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
                assertArrayEquals(floatSamples, readable.get(k).getSample(), 0.01f);
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
        parameters.put("TVLA", TVLA_STRING_VALUE);
        parameters.put("Param A", 16);
        parameters.put("XYZ offset", XYZ_TEST_VALUE);
        metaData.put(TRSTag.TRACE_SET_PARAMETERS, parameters);
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData).close();
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            TraceSetParameters readTraceSetParameters = (TraceSetParameters) readable.getMetaData().get(TRSTag.TRACE_SET_PARAMETERS);
            parameters.forEach((s, traceSetParameter) -> assertEquals(traceSetParameter, readTraceSetParameters.get(s)));
        }
    }

    /**
     * This tests adding a trace parameter that is not included in the definitions. This is expected to yield an
     * exception.
     *
     * @throws IOException
     * @throws TRSFormatException
     */
    @Test(expected = TRSFormatException.class)
    public void testWriteTraceParametersAndIncorrectDefinitions() throws IOException, TRSFormatException {
        TRSMetaData metaData = new TRSMetaData();
        metaData.put(TRSTag.TRS_VERSION, 2);
        TraceParameterDefinitions parameterDefinitions = new TraceParameterDefinitions();
        parameterDefinitions.put("XYZ1", new TraceParameterDefinition<>(XYZTestData.class, (short) 0));
        metaData.put(TRSTag.TRACE_PARAMETER_DEFINITIONS, parameterDefinitions);
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData)) {
            TraceParameters parameters = new TraceParameters();
            parameters.put("XYZ", XYZ_TEST_VALUE);
            traceWithParameters.add(Trace.create("", floatSamples, parameters));
        }
    }

    /**
     * This tests adding trace parameters, and their definitions. Note that it's not necessary to add the definitions
     * as a user (they are inferred).
     *
     * @throws IOException
     * @throws TRSFormatException
     */
    @Test
    public void testWriteTraceParametersAndDefinitions() throws IOException, TRSFormatException {
        TRSMetaData metaData = new TRSMetaData();
        metaData.put(TRSTag.TRS_VERSION, 2);
        TraceParameterDefinitions parameterDefinitions = new TraceParameterDefinitions();
        parameterDefinitions.put("XYZ", new TraceParameterDefinition<>(XYZTestData.class, (short) 0));
        metaData.put(TRSTag.TRACE_PARAMETER_DEFINITIONS, parameterDefinitions);
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData)) {
            for (int k = 0; k < 25; k++) {
                TraceParameters parameters = new TraceParameters();
                parameters.put("XYZ", new XYZTestData(k % 5, k / 5, k));
                traceWithParameters.add(Trace.create("", floatSamples, parameters));
            }
        }
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            for (int k = 0; k < 25; k++) {
                Trace trace = readable.get(k);
                final int test = k;
                parameterDefinitions.forEach((key, parameter) -> {
                    XYZTestData xyz = (XYZTestData) trace.getParameters().get(key);
                    assertEquals(xyz, new XYZTestData(test % 5, test / 5, test));
                });
            }
        }
    }

    @Test
    public void testWriteTraceParametersNoDefinitions() throws IOException, TRSFormatException {
        TRSMetaData metaData = new TRSMetaData();
        metaData.put(TRSTag.TRS_VERSION, 2);
        List<TraceParameters> testParameters = new ArrayList<>();
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData)) {
            for (int k = 0; k < 25; k++) {
                TraceParameters parameters = new TraceParameters();
                parameters.put("XYZ", new XYZTestData(k % 5, k / 5, k));
                traceWithParameters.add(Trace.create("", floatSamples, parameters));
                testParameters.add(parameters);
            }
        }
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            TraceParameterDefinitions parameterDefinitions = readable.getMetaData().getTraceParameterDefinitions();
            for (int k = 0; k < 25; k++) {
                Trace trace = readable.get(k);
                TraceParameters correctValue = testParameters.get(k);
                parameterDefinitions.forEach((key, parameter) -> {
                    TraceParameter casted = parameter.getType().cast(trace.getParameters().get(key));
                    assertEquals(casted, correctValue.get(key));
                });
            }
        }
    }

    /**
     * This tests adding a parameter with a name of 1000 characters
     *
     * @throws IOException
     * @throws TRSFormatException
     */
    @Test
    public void testWriteTraceParametersInvalidName() throws IOException, TRSFormatException {
        TRSMetaData metaData = new TRSMetaData();
        String parameterName = String.format("%1000s", "XYZ");
        metaData.put(TRSTag.TRS_VERSION, 2);
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData)) {
            TraceParameters parameters = new TraceParameters();
            parameters.put(parameterName, XYZ_TEST_VALUE);
            traceWithParameters.add(Trace.create("", floatSamples, parameters));
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
                parameters.put("BYTE", (byte)1);
                parameters.put("SHORT", (short)1);
                parameters.put("INT", 1);
                parameters.put("FLOAT", 1.0f);
                parameters.put("LONG", 1L);
                parameters.put("DOUBLE", 1.0d);
                parameters.put("STRING", "1");
                parameters.put("BYTEARRAY", new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
                parameters.put("SERIALIZABLE", new XYZDefaultTestData(k % 5, k / 5, k));
                traceWithParameters.add(Trace.create("", floatSamples, parameters));
                testParameters.add(parameters);
            }
        }
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            TraceParameterDefinitions parameterDefinitions = readable.getMetaData().getTraceParameterDefinitions();
            for (int k = 0; k < 25; k++) {
                Trace trace = readable.get(k);
                TraceParameters correctValue = testParameters.get(k);
                parameterDefinitions.forEach((key, parameter) -> {
                    TraceParameter casted = parameter.getType().cast(trace.getParameters().get(key));
                    assertEquals(casted, correctValue.get(key));
                });
            }
        }
    }

    /**
     * This tests the case where the reader of the trace does not have the class that was used
     * to create the set. In such a case, we still want to be able to read out the trace, even if we are not able
     * to determine the function of the parameter.
     *
     * @throws IOException
     * @throws TRSFormatException
     */
    @Test
    public void testReadUnknownTraceParameters() throws IOException, TRSFormatException {
        TRSMetaData metaData = new TRSMetaData();
        metaData.put(TRSTag.TRS_VERSION, 2);
        final int DATA_LENGTH = 2;
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData)) {
            for (int k = 0; k < 3; k++) {
                TraceParameters parameters = new TraceParameters();
                parameters.put("ABC", new TraceParameter() {
                    @Override
                    public byte[] serialize() {
                        return new byte[DATA_LENGTH];
                    }

                    @Override
                    public void deserialize(byte[] bytes) {

                    }

                    @Override
                    public int length() {
                        return DATA_LENGTH;
                    }
                });
                traceWithParameters.add(Trace.create("", floatSamples, parameters));
            }
        }
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            TraceParameterDefinitions parameterDefinitions = readable.getMetaData().getTraceParameterDefinitions();
            for (int k = 0; k < 3; k++) {
                Trace trace = readable.get(k);
                parameterDefinitions.forEach((key, parameter) -> {
                    TraceParameter abc = trace.getParameters().get(key);
                    assertEquals(DATA_LENGTH, abc.length());
                });
                assertEquals(DATA_LENGTH, trace.getData().length);
            }
        }
    }
}
