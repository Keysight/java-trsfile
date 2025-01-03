import com.riscure.trs.TRSFormatException;
import com.riscure.trs.TRSMetaData;
import com.riscure.trs.Trace;
import com.riscure.trs.TraceSet;
import com.riscure.trs.enums.Encoding;
import com.riscure.trs.enums.ParameterType;
import com.riscure.trs.enums.TRSTag;
import com.riscure.trs.io.LittleEndianInputStream;
import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.ByteArrayParameter;
import com.riscure.trs.parameter.trace.TraceParameterMap;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinition;
import com.riscure.trs.parameter.trace.definition.TraceParameterDefinitionMap;
import com.riscure.trs.parameter.traceset.TraceSetParameter;
import com.riscure.trs.parameter.traceset.TraceSetParameterMap;
import com.riscure.trs.types.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class TestTraceSet {
    private static Path tempDir;
    private static final String BYTES_TRS = "bytes.trs";
    private static final String SHORTS_TRS = "shorts.trs";
    private static final String INTS_TRS = "ints.trs";
    private static final String FLOATS_TRS = "floats.trs";
    private static final String TRS = ".trs";
    private static final int NUMBER_OF_TRACES = 1024;
    private static final float[] BYTE_SAMPLES = new float[]{1, 2, 3, 4, 5};
    private static final float[] SHORT_SAMPLES = new float[]{1, 2, 3, 4, Byte.MAX_VALUE + 1};
    private static final float[] INT_SAMPLES = new float[]{1, 2, 3, 4, Short.MAX_VALUE + 1};
    private static final float[] FLOAT_SAMPLES = new float[]{1, 2, 3, 4, 5.1f};
    private static final String TVLA_STRING_VALUE = "Trace set contains the following TVLA sets: Random, R5S-Box_Out\n" +
            "AES-128 ENCRYPT (Input -> Output) Round 5 S-Box Out:HW(3~7)";

    @BeforeAll
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

    @AfterAll
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
    void testOpenBytes() throws IOException, TRSFormatException {
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
    void testOpenShorts() throws IOException, TRSFormatException {
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
    void testOpenInts() throws IOException, TRSFormatException {
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
    void testOpenFloats() throws IOException, TRSFormatException {
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

    @Test
    void testUTF8Title() throws IOException, TRSFormatException {
        String title = "씨브 크레그스만";
        String name = UUID.randomUUID().toString() + TRS;
        try (TraceSet ts = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            ts.add(Trace.create(title, new float[0], new TraceParameterMap()));
        } catch (TRSFormatException e) {
            throw e;
        }
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            assertEquals(title, readable.get(0).getTitle());
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
    void testWriteTraceSetParameters() throws IOException, TRSFormatException {
        TRSMetaData metaData = TRSMetaData.create();
        TraceSetParameterMap parameters = new TraceSetParameterMap();
        parameters.put("BYTE", (byte) 1);
        parameters.put("SHORT", (short) 2);
        parameters.put("INT", 3);
        parameters.put("FLOAT", (float) 4);
        parameters.put("LONG", (long) 5);
        parameters.put("DOUBLE", (double) 6);
        parameters.put("STRING", String.format("%3d", 7));
        parameters.put("BOOLEAN", true);
        parameters.put("BYTEARRAY", new byte[]{(byte) 8, (byte) 9, (byte) 0});
        parameters.put("SHORTARRAY", new short[]{(short) 1, (short) 2, (short) 3});
        parameters.put("INTARRAY", new int[]{4, 5, 6});
        parameters.put("FLOATARRAY", new float[]{(float) 7, (float) 8, (float) 9});
        parameters.put("LONGARRAY", new long[]{0, 1, 2});
        parameters.put("DOUBLEARRAY", new double[]{3, 4, 5});
        parameters.put("BOOLEANARRAY", new boolean[]{true, false, true, false, true, true});
        parameters.put("TVLA", TVLA_STRING_VALUE);
        //parameters.put("XYZ offset", XYZ_TEST_VALUE);
        metaData.put(TRSTag.TRACE_SET_PARAMETERS, parameters);
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData).close();
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            TraceSetParameterMap readTraceSetParameterMap = readable.getMetaData().getTraceSetParameters();
            parameters.forEach((s, traceSetParameter) -> assertEquals(traceSetParameter, readTraceSetParameterMap.get(s)));
        }
    }

    /**
     * This tests adding a parameter with a name of 100000 characters
     *
     * @throws IOException
     * @throws TRSFormatException
     */
    @Test
    void testWriteTraceParametersInvalidName() throws IOException, TRSFormatException {
        TRSMetaData metaData = TRSMetaData.create();
        String parameterName = String.format("%100000s", "XYZ");
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData)) {
            TraceParameterMap parameters = new TraceParameterMap();
            parameters.put(parameterName, 1);
            traceWithParameters.add(Trace.create("", FLOAT_SAMPLES, parameters));
        }
        //READ BACK AND CHECK RESULT
        assertThrows(TRSFormatException.class, () -> {
            try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
                TraceParameterDefinitionMap parameterDefinitions = readable.getMetaData().getTraceParameterDefinitions();
                parameterDefinitions.forEach((key, parameter) -> assertEquals(parameterName, key));
            }
        });
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
     void testWriteTraceParametersVaryingStringLength() throws IOException, TRSFormatException {
        TRSMetaData metaData = TRSMetaData.create();
        List<TraceParameterMap> testParameters = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        strings.add("abcd");
        strings.add("abcdefgh");
        strings.add("ab");
        strings.add("abcdefgh汉字");
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData)) {
            for (int k = 0; k < 25; k++) {
                TraceParameterMap parameters = new TraceParameterMap();
                parameters.put("BYTEARRAY", new byte[]{(byte) k, (byte) k, (byte) k});
                parameters.put(TraceParameter.SAMPLES, new float[]{(float) k, (float) k, (float) k});
                parameters.put(TraceParameter.TITLE, strings.get(k % strings.size()));
                traceWithParameters.add(Trace.create(strings.get(k % strings.size()), FLOAT_SAMPLES, parameters));
                testParameters.add(parameters);
            }
        }
        //READ BACK AND CHECK RESULT
        readBackGeneric(testParameters, name);
    }

    /**
     * This tests whether all getters are working as expected
     *
     * @throws IOException
     * @throws TRSFormatException
     */
    @Test
    void testReadTraceParametersTyped() throws IOException, TRSFormatException {
        TRSMetaData metaData = TRSMetaData.create();
        List<TraceParameterMap> testParameters = new ArrayList<>();
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData)) {
            for (int k = 0; k < 25; k++) {
                TraceParameterMap parameters = new TraceParameterMap();
                parameters.put("BYTE", (byte) k);
                parameters.put("SHORT", (short) k);
                parameters.put("INT", k);
                parameters.put("FLOAT", (float) k);
                parameters.put("LONG", (long) k);
                parameters.put("DOUBLE", (double) k);
                parameters.put("STRING", String.format("%3d", k));
                parameters.put("BOOLEAN", true);
                parameters.put("BYTEARRAY", new byte[]{(byte) k, (byte) k, (byte) k});
                parameters.put("SHORTARRAY", new short[]{(short) k, (short) k, (short) k});
                parameters.put("INTARRAY", new int[]{k, k, k});
                parameters.put("FLOATARRAY", new float[]{(float) k, (float) k, (float) k});
                parameters.put("LONGARRAY", new long[]{k, k, k});
                parameters.put("DOUBLEARRAY", new double[]{k, k, k});
                parameters.put("BOOLEANARRAY", new boolean[]{true, false, true, false, true, true});
                traceWithParameters.add(Trace.create("", FLOAT_SAMPLES, parameters));
                testParameters.add(parameters);
            }
        }
        readBackGeneric(testParameters, name);
        readBackTyped(testParameters, name);
        readBackTypedKeys(testParameters, name);
    }

    private void readBackGeneric(List<TraceParameterMap> testParameters, String name) throws IOException, TRSFormatException {
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            TraceParameterDefinitionMap parameterDefinitions = readable.getMetaData().getTraceParameterDefinitions();
            for (int k = 0; k < 25; k++) {
                assertEquals(parameterDefinitions.size(), testParameters.get(k).size());
                Trace trace = readable.get(k);
                TraceParameterMap correctValue = testParameters.get(k);
                parameterDefinitions.forEach((key, parameter) -> {
                    TraceParameter traceParameter = trace.getParameters().get(key);
                    assertEquals(traceParameter, correctValue.get(key));
                });
            }
        }
    }

    private void readBackTyped(List<TraceParameterMap> testParameters, String name) throws IOException, TRSFormatException {
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            TraceParameterDefinitionMap parameterDefinitions = readable.getMetaData().getTraceParameterDefinitions();
            for (int k = 0; k < 25; k++) {
                assertEquals(parameterDefinitions.size(), testParameters.get(k).size());
                Trace trace = readable.get(k);
                TraceParameterMap correctValue = testParameters.get(k);
                parameterDefinitions.forEach((key, parameter) -> {
                    switch (parameter.getType()) {
                        case BYTE:
                            if (parameter.getLength() == 1) {
                                assertEquals(correctValue.getByte(key), trace.getParameters().getByte(key));
                            } else {
                                assertArrayEquals(correctValue.getByteArray(key), trace.getParameters().getByteArray(key));
                            }
                            break;
                        case SHORT:
                            if (parameter.getLength() == 1) {
                                assertEquals(correctValue.getShort(key), trace.getParameters().getShort(key));
                            } else {
                                assertArrayEquals(correctValue.getShortArray(key), trace.getParameters().getShortArray(key));
                            }
                            break;
                        case INT:
                            if (parameter.getLength() == 1) {
                                assertEquals(correctValue.getInt(key), trace.getParameters().getInt(key));
                            } else {
                                assertArrayEquals(correctValue.getIntArray(key), trace.getParameters().getIntArray(key));
                            }
                            break;
                        case FLOAT:
                            if (parameter.getLength() == 1) {
                                assertEquals(correctValue.getFloat(key), trace.getParameters().getFloat(key), 0.01f);
                            } else {
                                assertArrayEquals(correctValue.getFloatArray(key), trace.getParameters().getFloatArray(key), 0.01f);
                            }
                            break;
                        case LONG:
                            if (parameter.getLength() == 1) {
                                assertEquals(correctValue.getLong(key), trace.getParameters().getLong(key));
                            } else {
                                assertArrayEquals(correctValue.getLongArray(key), trace.getParameters().getLongArray(key));
                            }
                            break;
                        case DOUBLE:
                            if (parameter.getLength() == 1) {
                                assertEquals(correctValue.getDouble(key), trace.getParameters().getDouble(key), 0.01);
                            } else {
                                assertArrayEquals(correctValue.getDoubleArray(key), trace.getParameters().getDoubleArray(key), 0.01);
                            }
                            break;
                        case STRING:
                            assertEquals(correctValue.getString(key), trace.getParameters().getString(key));
                            break;
                        case BOOL:
                            if (parameter.getLength() == 1) {
                                assertEquals(correctValue.getBoolean(key), trace.getParameters().getBoolean(key));
                            } else {
                                assertArrayEquals(correctValue.getBooleanArray(key), trace.getParameters().getBooleanArray(key));
                            }
                            break;
                        default:
                            throw new RuntimeException("Unexpected type: " + parameter.getType());
                    }
                });
            }
        }
    }

    private void readBackTypedKeys(List<TraceParameterMap> testParameters, String name) throws IOException, TRSFormatException {
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            TraceParameterDefinitionMap parameterDefinitions = readable.getMetaData().getTraceParameterDefinitions();
            for (int k = 0; k < 25; k++) {
                assertEquals(parameterDefinitions.size(), testParameters.get(k).size());
                Trace trace = readable.get(k);
                TraceParameterMap correctValue = testParameters.get(k);
                parameterDefinitions.forEach((key, parameter) -> {
                    TypedKey<?> typedKey;
                    switch (parameter.getType()) {
                        case BYTE:
                            typedKey = parameter.getLength() > 1 ? new ByteArrayTypeKey(key) : new ByteTypeKey(key);
                            break;
                        case SHORT:
                            typedKey = parameter.getLength() > 1 ? new ShortArrayTypeKey(key) : new ShortTypeKey(key);
                            break;
                        case INT:
                            typedKey = parameter.getLength() > 1 ? new IntegerArrayTypeKey(key) : new IntegerTypeKey(key);
                            break;
                        case FLOAT:
                            typedKey = parameter.getLength() > 1 ? new FloatArrayTypeKey(key) : new FloatTypeKey(key);
                            break;
                        case LONG:
                            typedKey = parameter.getLength() > 1 ? new LongArrayTypeKey(key) : new LongTypeKey(key);
                            break;
                        case DOUBLE:
                            typedKey = parameter.getLength() > 1 ? new DoubleArrayTypeKey(key) : new DoubleTypeKey(key);
                            break;
                        case STRING:
                            typedKey = new StringTypeKey(key);
                            break;
                        case BOOL:
                            typedKey = parameter.getLength() > 1 ? new BooleanArrayTypeKey(key) : new BooleanTypeKey(key);
                            break;
                        default:
                            throw new RuntimeException("Unexpected type: " + parameter.getType());
                    }
                    if (parameter.getLength() > 1 && typedKey.getCls().isArray()) {
                        assertArrayEquals(Arrays.asList(correctValue.getOrElseThrow(typedKey)).toArray(),
                                Arrays.asList(trace.getParameters().getOrElseThrow(typedKey)).toArray());
                    } else {
                        assertEquals(correctValue.get(typedKey), trace.getParameters().get(typedKey));
                    }
                });
            }
        }
    }

    /**
     * This tests getting a value of the wrong type correctly throws an exception
     *
     * @throws IOException
     * @throws TRSFormatException
     */
    @Test
    void testExceptionWrongType() throws IOException, TRSFormatException {
        TRSMetaData metaData = TRSMetaData.create();
        //CREATE TRACE
        String name = UUID.randomUUID().toString() + TRS;
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name, metaData)) {
            TraceParameterMap parameters = new TraceParameterMap();
            parameters.put("BYTE", (byte) 1);
            traceWithParameters.add(Trace.create("", FLOAT_SAMPLES, parameters));
        }
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            assertThrows(ClassCastException.class, () -> readable.get(0).getParameters().getDouble("BYTE"));
        }
    }

    /**
     * This
     * @throws IOException
     * @throws TRSFormatException
     */
    @Test
    void testContainsNonArray() throws IOException, TRSFormatException {
        ByteTypeKey byteKey = new ByteTypeKey("BYTE");
        String name = UUID.randomUUID().toString() + TRS;
        try (TraceSet traceWithParameters = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            TraceParameterMap parameters = new TraceParameterMap();
            parameters.put(byteKey, (byte) 1);
            traceWithParameters.add(Trace.create("", FLOAT_SAMPLES, parameters));
        }
        //READ BACK AND CHECK RESULT
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + name)) {
            assertTrue(readable.get(0).getParameters().get(byteKey).isPresent());
        }
    }

    /**
     * This test checks whether all deserialize methods throw an exception if the inputstream does not contain enough data
     * Introduced to test #11: TraceParameter.deserialize does not check the actual returned length
     */
    @Test
    void testInvalidParameterLength() {
        int errors = 0;
        byte[] empty = new byte[1];
        for (ParameterType type : ParameterType.values()) {
            try (LittleEndianInputStream dis = new LittleEndianInputStream(new ByteArrayInputStream(empty))) {
                TraceParameter.deserialize(type, (short) 4, dis);
            } catch (IOException e) {
                errors++;
            }
        }
        assertEquals(ParameterType.values().length, errors);
    }

    /**
     * This test was added to test #26: Trace(Set)ParameterMap is modifiable in read only mode
     */
    @Test
    void testModificationAfterReadback() throws IOException, TRSFormatException {
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + BYTES_TRS)) {
            assertThrows(UnsupportedOperationException.class, () -> readable.getMetaData().getTraceSetParameters().put("SHOULD_FAIL", 0));
            assertThrows(UnsupportedOperationException.class, () -> readable.getMetaData().getTraceParameterDefinitions().put("SHOULD_FAIL", new TraceParameterDefinition<TraceParameter>(ParameterType.BYTE, (short)1, (short)1)));
            for (int k = 0; k < NUMBER_OF_TRACES; k++) {
                Trace t = readable.get(k);
                assertThrows(UnsupportedOperationException.class, () -> t.getParameters().put("SHOULD_FAIL", 0));
            }
        }
    }

    /**
     * This test checks whether an empty array is serialized and deserialized correctly
     * Expected: an exception is thrown when adding an empty parameter
     */
    @Test
    void testEmptyArrayParameter() {
        assertThrows(IllegalArgumentException.class, () -> new TraceParameterMap().put("EMPTY", new byte[0]));
    }

    /**
     * This test checks whether a Trace(Set)ParameterMap correctly works with typed keys
     */
    @Test
    void testContainsTypedKey() {
        TraceParameterMap tpm = new TraceParameterMap();
        TraceSetParameterMap tspm = new TraceSetParameterMap();
        String rawKey = "BYTE";
        ByteTypeKey typedKey = new ByteTypeKey(rawKey);
        tpm.put(typedKey, (byte)1);
        tspm.put(typedKey, (byte)2);

        assertTrue(tpm.get(typedKey).isPresent());
        assertTrue(tspm.get(typedKey).isPresent());
    }

    /**
     * This test checks whether you can get an array type of a simple value
     */
    @Test
    void testGetArrayOfLengthOne() {
        TraceParameterMap tpm = new TraceParameterMap();
        TraceSetParameterMap tspm = new TraceSetParameterMap();

        byte rawValue = 1;
        String rawKey = "BYTE";
        ByteTypeKey typedKey = new ByteTypeKey(rawKey);
        tpm.put(typedKey, rawValue);
        tspm.put(typedKey, rawValue);

        ByteArrayTypeKey arrayTypeKey = new ByteArrayTypeKey(rawKey);
        assertTrue(tpm.get(arrayTypeKey).isPresent());
        assertTrue(tspm.get(arrayTypeKey).isPresent());
        assertArrayEquals(new byte[]{rawValue}, tpm.getByteArray(rawKey));
        assertArrayEquals(new byte[]{rawValue}, tspm.getByteArray(rawKey));
    }

    /**
     * This test checks whether a Trace(Set)ParameterMap correctly fails with a typed key of the incorrect type
     */
    @Test
    void testContainsWrongTypedKey() {
        TraceParameterMap tpm = new TraceParameterMap();
        TraceSetParameterMap tspm = new TraceSetParameterMap();
        String rawKey = "BYTE";
        ByteTypeKey typedKey = new ByteTypeKey(rawKey);
        tpm.put(rawKey, new byte[]{1, 2});     //actually a byte array
        tspm.put(rawKey, 2);     //actually an int

        assertThrows(ClassCastException.class, () -> tpm.get(typedKey));
        assertThrows(ClassCastException.class, () -> tspm.get(typedKey));
    }

    /**
     * This test ensure that the underlying data of a copied map cannot change underlying map itself
     */
    @Test
    void testCopyConstructor() {
        byte[] ba = {1, 2, 3, 4, 5};
        ByteArrayParameter bap = new ByteArrayParameter(ba);
        TraceParameterMap tpm = new TraceParameterMap();
        tpm.put("BA", bap);
        TraceParameterMap copy = tpm.copy();
        ba[1] = 6;
        byte[] baCopy = (byte[]) copy.get("BA").getValue();
        assertFalse(Arrays.equals(baCopy, ba), "Arrays should not be equal, but they are");
    }

    @Test
    void testEmptyString() {
        TraceParameterMap tpm = new TraceParameterMap();
        tpm.put("EMPTY_STRING", "");
        TraceParameterDefinitionMap tpdm = TraceParameterDefinitionMap.createFrom(tpm);
        byte[] serialized = tpm.toByteArray();

        TraceParameterMap deserialized = TraceParameterMap.deserialize(serialized, tpdm);
        String empty_string = deserialized.get("EMPTY_STRING").toString();
        assertEquals("", empty_string);
    }

    @Test
    void testTooLargeArraySetParameter() {
        int arrayLength = 65536;
        IntegerArrayTypeKey key = new IntegerArrayTypeKey("TOO_LARGE_ARRAY");
        TraceSetParameterMap tspm = new TraceSetParameterMap();
        tspm.put(key, new int[arrayLength]);
        assertThrows(RuntimeException.class, tspm::serialize);
    }

    @Test
    void testLargeArraySetParameter() {
        int arrayLength = 65535;
        IntegerArrayTypeKey key = new IntegerArrayTypeKey("JUST_SMALL_ENOUGH_ARRAY");
        TraceSetParameterMap tspm = new TraceSetParameterMap();
        tspm.put(key, new int[arrayLength]);
        byte[] serialize = tspm.serialize();
        TraceSetParameterMap deserialize = TraceSetParameterMap.deserialize(serialize);
        assertEquals(arrayLength, deserialize.getOrElseThrow(key).length);
    }

    @Test
    void testReadOneBeyondTracesetLimit() throws IOException, TRSFormatException {
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath() + File.separator + BYTES_TRS)) {
            int numberOfTracesRead = readable.getMetaData().getInt(TRSTag.NUMBER_OF_TRACES);
            Encoding encoding = Encoding.fromValue(readable.getMetaData().getInt(TRSTag.SAMPLE_CODING));
            assertEquals(Encoding.BYTE, encoding);
            assertEquals(NUMBER_OF_TRACES, numberOfTracesRead);
            assertTrue(
                    assertThrows(IllegalArgumentException.class, () -> readable.get(NUMBER_OF_TRACES))
                            .getMessage().startsWith("Requested trace index"));
        }
    }

    @Test
    void testReadTwoBeyondTracesetLimit() throws IOException, TRSFormatException {
        try (TraceSet readable = TraceSet.open(tempDir.toAbsolutePath() + File.separator + BYTES_TRS)) {
            int numberOfTracesRead = readable.getMetaData().getInt(TRSTag.NUMBER_OF_TRACES);
            Encoding encoding = Encoding.fromValue(readable.getMetaData().getInt(TRSTag.SAMPLE_CODING));
            assertEquals(Encoding.BYTE, encoding);
            assertEquals(NUMBER_OF_TRACES, numberOfTracesRead);
            assertTrue(
                    assertThrows(IllegalArgumentException.class, () -> readable.get(NUMBER_OF_TRACES + 1))
                            .getMessage().startsWith("Requested trace index"));
        }
    }

    /**
     * Test to reproduce Github issue #61: Reading a legacy trace with a 0-length data field
     */
    @Test
    void test61ReadLegacyTraceWithoutData() throws IOException, TRSFormatException {
        Path filePath = tempDir.resolve("test_issue_61.trs");
        TRSMetaData metaData = new TRSMetaData();
        metaData.put(TRSTag.TRS_VERSION, 1);
        try (TraceSet ts = TraceSet.create(filePath.toString(), metaData)) {
            ts.add(new Trace(new float[]{}));
        }
        try (TraceSet ts = TraceSet.open(filePath.toString())) {
            assertDoesNotThrow(() -> ts.get(0));
        }
    }

    /**
     * Test to reproduce Github issue #65: TRS files remain 'in use' after they have been closed
     * Test this issue by opening and closing a file, and then checking whether the file can be deleted.
     */
    @Test
    void testFileReleasing() throws IOException, TRSFormatException, InterruptedException {
        String filePath = tempDir.toAbsolutePath() + File.separator + BYTES_TRS;
        // Open the file, and let the try-with-resources statement close it
        try (TraceSet traceSet = TraceSet.open(filePath)) {
            traceSet.getMetaData().getTraceSetParameters();
        }
        // Unfortunately, the current solution requires a garbage collect to have been performed before the issue is resolved.
        // Other fixes required either a Java 8 Cleaner.clean() call not accessible from Java 21, or a Java 20 Arena.close(),
        // which is not been finalized in Java 21.
        System.gc();
        Thread.sleep(1000);
        // Assert that the opened file has been closed again, by deleting it.
        File file = new File(filePath);
        assert(file.delete());
    }
}
