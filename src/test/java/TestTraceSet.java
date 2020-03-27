import com.riscure.trs.TRSFormatException;
import com.riscure.trs.Trace;
import com.riscure.trs.TraceSet;
import com.riscure.trs.enums.Encoding;
import com.riscure.trs.enums.TRSTag;
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
    private static String testBytes = "bytes.trs";
    private static String testShorts = "shorts.trs";
    private static String testInts = "ints.trs";
    private static String testFloats = "floats.trs";
    private static final int numberOfTraces = 1024;
    private static final float[] byteSamples = new float[]{1, 2, 3, 4, 5};
    private static final float[] shortSamples = new float[]{1, 2, 3, 4, Byte.MAX_VALUE+1};
    private static final float[] intSamples = new float[]{1, 2, 3, 4, Short.MAX_VALUE+1};
    private static final float[] floatSamples = new float[]{1, 2, 3, 4, 5.1f};

    @BeforeClass
    public static void createTempDir() throws IOException, TRSFormatException {
        tempDir = Files.createTempDirectory("TestTraceSet");

        TraceSet writable = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + testBytes);
        for (int k = 0; k < numberOfTraces; k++) {
            writable.add(Trace.create(byteSamples));
        }
        writable.close();

        writable = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + testShorts);
        for (int k = 0; k < numberOfTraces; k++) {
            writable.add(Trace.create(shortSamples));
        }
        writable.close();

        writable = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + testInts);
        for (int k = 0; k < numberOfTraces; k++) {
            writable.add(Trace.create(intSamples));
        }
        writable.close();

        writable = TraceSet.create(tempDir.toAbsolutePath().toString() + File.separator + testFloats);
        for (int k = 0; k < numberOfTraces; k++) {
            writable.add(Trace.create(floatSamples));
        }
        writable.close();
    }

    @AfterClass
    public static void cleanup() throws InterruptedException {
        //We need to allow a little time for java to release all handles
        System.gc();
        Thread.sleep(100);
        for (File file : Objects.requireNonNull(tempDir.toFile().listFiles())) {
            if (!file.delete()) System.err.printf("Failed to delete temporary file '%s'\n", file.toPath().toAbsolutePath().toString());
        }
        if (!tempDir.toFile().delete()) System.err.printf("Failed to delete temporary folder '%s'\n", tempDir.toFile().toPath().toAbsolutePath().toString());
    }

    @Test
    public void testOpenBytes() throws IOException, TRSFormatException {
        TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + testBytes);
        int numberOfTracesRead = readable.getMetaData().getInt(TRSTag.NUMBER_OF_TRACES);
        Encoding encoding = Encoding.fromValue(readable.getMetaData().getInt(TRSTag.SAMPLE_CODING));
        assertEquals(Encoding.BYTE, encoding);
        assertEquals(numberOfTracesRead, numberOfTraces);
        for (int k = 0; k < numberOfTraces; k++) {
            Trace t = readable.get(k);
            assertEquals(Encoding.BYTE.getValue(), t.getPreferredCoding());
            assertArrayEquals(byteSamples, readable.get(k).getSample(), 0.01f);
        }
        readable.close();
    }

    @Test
    public void testOpenShorts() throws IOException, TRSFormatException {
        TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + testShorts);
        int numberOfTracesRead = readable.getMetaData().getInt(TRSTag.NUMBER_OF_TRACES);
        Encoding encoding = Encoding.fromValue(readable.getMetaData().getInt(TRSTag.SAMPLE_CODING));
        assertEquals(Encoding.SHORT, encoding);
        assertEquals(numberOfTracesRead, numberOfTraces);
        for (int k = 0; k < numberOfTraces; k++) {
            Trace t = readable.get(k);
            assertEquals(Encoding.SHORT.getValue(), t.getPreferredCoding());
            assertArrayEquals(shortSamples, readable.get(k).getSample(), 0.01f);
        }
        readable.close();
    }

    @Test
    public void testOpenInts() throws IOException, TRSFormatException {
        TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + testInts);
        int numberOfTracesRead = readable.getMetaData().getInt(TRSTag.NUMBER_OF_TRACES);
        Encoding encoding = Encoding.fromValue(readable.getMetaData().getInt(TRSTag.SAMPLE_CODING));
        assertEquals(Encoding.INT, encoding);
        assertEquals(numberOfTracesRead, numberOfTraces);
        for (int k = 0; k < numberOfTraces; k++) {
            Trace t = readable.get(k);
            assertEquals(Encoding.INT.getValue(), t.getPreferredCoding());
            assertArrayEquals(intSamples, readable.get(k).getSample(), 0.01f);
        }
        readable.close();
    }

    @Test
    public void testOpenFloats() throws IOException, TRSFormatException {
        TraceSet readable = TraceSet.open(tempDir.toAbsolutePath().toString() + File.separator + testFloats);
        int numberOfTracesRead = readable.getMetaData().getInt(TRSTag.NUMBER_OF_TRACES);
        Encoding encoding = Encoding.fromValue(readable.getMetaData().getInt(TRSTag.SAMPLE_CODING));
        assertEquals(Encoding.FLOAT, encoding);
        assertEquals(numberOfTracesRead, numberOfTraces);
        for (int k = 0; k < numberOfTraces; k++) {
            Trace t = readable.get(k);
            assertEquals(Encoding.FLOAT.getValue(), t.getPreferredCoding());
            assertArrayEquals(floatSamples, readable.get(k).getSample(), 0.01f);
        }
        readable.close();
    }
}
