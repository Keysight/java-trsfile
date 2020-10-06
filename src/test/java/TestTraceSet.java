import com.riscure.trs.TRSFormatException;
import com.riscure.trs.TRSMetaData;
import com.riscure.trs.Trace;
import com.riscure.trs.TraceSet;
import com.riscure.trs.enums.Encoding;
import com.riscure.trs.enums.TRSTag;
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
import java.util.Objects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestTraceSet {
    private static Path tempDir;
    private static final String BYTES_TRS = "bytes.trs";
    private static final String SHORTS_TRS = "shorts.trs";
    private static final String INTS_TRS = "ints.trs";
    private static final String FLOATS_TRS = "floats.trs";
    private static final String TRACE_PARAMETERS_TRS = "traceParameters.trs";
    private static final String TRACE_PARAMETERS_GENERIC_TRS = "traceParametersGeneric.trs";
    private static final String TRACE_PARAMETERS_WITH_DEFINITION_TRS = "traceParametersWithDefinition.trs";
    private static final String TRACE_SET_PARAMETERS_TRS = "traceSetParameters.trs";
    private static final int NUMBER_OF_TRACES = 1024;
    private static final float[] byteSamples = new float[]{1, 2, 3, 4, 5};
    private static final float[] shortSamples = new float[]{1, 2, 3, 4, Byte.MAX_VALUE+1};
    private static final float[] intSamples = new float[]{1, 2, 3, 4, Short.MAX_VALUE+1};
    private static final float[] floatSamples = new float[]{1, 2, 3, 4, 5.1f};

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
            assertEquals(numberOfTracesRead, NUMBER_OF_TRACES);
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
            assertEquals(numberOfTracesRead, NUMBER_OF_TRACES);
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
            assertEquals(numberOfTracesRead, NUMBER_OF_TRACES);
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
            assertEquals(numberOfTracesRead, NUMBER_OF_TRACES);
            for (int k = 0; k < NUMBER_OF_TRACES; k++) {
                Trace t = readable.get(k);
                assertEquals(Encoding.FLOAT.getValue(), t.getPreferredCoding());
                assertArrayEquals(floatSamples, readable.get(k).getSample(), 0.01f);
            }
        }
    }

    @Test
    public void testWriteTraceSetParameters() throws IOException, TRSFormatException {
        TRSMetaData metaData = new TRSMetaData();
        metaData.put(TRSTag.TRS_VERSION, 2);
        TraceSetParameters parameters = new TraceSetParameters();
        parameters.put("TVLA", "Trace set contains the following TVLA sets: Random, R5S-Box_Out\n" +
                "AES-128 ENCRYPT (Input -> Output) Round 5 S-Box Out:HW(3~7)");
        parameters.put("Param A", 16);
        parameters.put("XYZ offset", new XYZTestData(0, 1, 2));
        metaData.put(TRSTag.TRACE_SET_PARAMETERS, parameters);
        //CREATE TRACE
        TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + TRACE_SET_PARAMETERS_TRS, metaData).close();
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + TRACE_SET_PARAMETERS_TRS)) {
            TraceSetParameters readTraceSetParameters = (TraceSetParameters) readable.getMetaData().get(TRSTag.TRACE_SET_PARAMETERS);
            parameters.forEach((s, traceSetParameter) -> assertEquals(traceSetParameter, readTraceSetParameters.get(s)));
        }
    }

    @Test
    public void testWriteTraceParametersAndDefinitions() throws IOException, TRSFormatException {
        TRSMetaData metaData = new TRSMetaData();
        metaData.put(TRSTag.TRS_VERSION, 2);
        TraceParameterDefinitions parameterDefinitions = new TraceParameterDefinitions();
        parameterDefinitions.put("XYZ", new TraceParameterDefinition<>(XYZTestData.class, (short) 0));
        metaData.put(TRSTag.TRACE_PARAMETER_DEFINITIONS, parameterDefinitions);
        //CREATE TRACE
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + TRACE_PARAMETERS_WITH_DEFINITION_TRS, metaData)) {
            for (int k = 0; k < 25; k++) {
                TraceParameters parameters = new TraceParameters();
                parameters.put("XYZ", new XYZTestData(k % 5, k / 5, k));
                traceWithParameters.add(Trace.create("", floatSamples, parameters));
            }
        }
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + TRACE_PARAMETERS_WITH_DEFINITION_TRS)) {
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
        //CREATE TRACE
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + TRACE_PARAMETERS_TRS, metaData)) {
            for (int k = 0; k < 25; k++) {
                TraceParameters parameters = new TraceParameters();
                parameters.put("XYZ", new XYZTestData(k % 5, k / 5, k));
                traceWithParameters.add(Trace.create("", floatSamples, parameters));
            }
        }
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + TRACE_PARAMETERS_TRS)) {
            TraceParameterDefinitions parameterDefinitions = readable.getMetaData().getTraceParameterDefinitions();
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
    public void testWriteGenericTraceParameters() throws IOException, TRSFormatException {
        TRSMetaData metaData = new TRSMetaData();
        metaData.put(TRSTag.TRS_VERSION, 2);
        //CREATE TRACE
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + TRACE_PARAMETERS_GENERIC_TRS, metaData)) {
            for (int k = 0; k < 25; k++) {
                TraceParameters parameters = new TraceParameters();
                parameters.put("XYZ", new XYZGenericTestData(k % 5, k / 5, k));
                traceWithParameters.add(Trace.create("", floatSamples, parameters));
            }
        }
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + TRACE_PARAMETERS_GENERIC_TRS)) {
            TraceParameterDefinitions parameterDefinitions = readable.getMetaData().getTraceParameterDefinitions();
            for (int k = 0; k < 25; k++) {
                Trace trace = readable.get(k);
                final int test = k;
                parameterDefinitions.forEach((key, parameter) -> {
                    XYZGenericTestData xyz = (XYZGenericTestData) trace.getParameters().get(key);
                    assertEquals(xyz, new XYZGenericTestData(test % 5, test / 5, test));
                });
            }
        }
    }
}
