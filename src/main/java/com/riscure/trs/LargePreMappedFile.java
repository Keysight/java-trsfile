package com.riscure.trs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class LargePreMappedFile implements AutoCloseable{
    private final FileChannel channel;

    private final List<MappedBuffer> buffers = new ArrayList<>();
    private final long readOffset;
    private final long traceSize;
    private final long fileSize;

    private long totalBufferSize;

    public LargePreMappedFile(FileChannel channel, long metaDataSize, long traceSize) throws IOException {
        this.channel = channel;
        this.readOffset = metaDataSize;
        this.traceSize = traceSize;
        this.fileSize = channel.size() - readOffset;

        mapBuffers();
    }

    public ByteBuffer getBuffer(int index) {
        if (traceSize == 0) {
            return ByteBuffer.wrap(new byte[0]);
        }
        return findBufferAndMoveToTrace(index);
    }

    private ByteBuffer findBufferAndMoveToTrace(int traceIndex) {
        MappedBuffer mappedBuffer = buffers.stream()
                .filter(buffer -> traceIndex >= buffer.getFirstTraceIndex() &&
                        traceIndex < buffer.getFirstTraceIndex() + buffer.getNumberOfTraces())
                .findFirst()
                .orElseThrow();
        ByteBuffer buffer = mappedBuffer.getBuffer();
        int traceIndexInBuffer = traceIndex - mappedBuffer.getFirstTraceIndex();
        long positionInBuffer = traceIndexInBuffer * traceSize;
        buffer.position((int) positionInBuffer);
        return buffer;
    }

    private void mapBuffers() {
        if (traceSize > 0) {
            int tracesPerBuffer = (int) (Integer.MAX_VALUE / traceSize);
            long maximumBufferSize = tracesPerBuffer * traceSize;

            int firstTraceIndex = 0;
            for (long offset = 0; offset < fileSize; offset += maximumBufferSize) {
                buffers.add(mapBuffer(firstTraceIndex, maximumBufferSize));
                firstTraceIndex += tracesPerBuffer;
            }
        }
    }

    private MappedBuffer mapBuffer(int firstTraceIndex, long bufferSize) {
        try {
            long bufferStart = firstTraceIndex * traceSize;
            this.totalBufferSize += bufferSize;
            long limitedBufferSize = Math.min(fileSize - bufferStart, bufferSize);
            MappedBuffer mappedBuffer = new MappedBuffer(this.channel.map(FileChannel.MapMode.READ_ONLY, readOffset + bufferStart, limitedBufferSize),
                    firstTraceIndex,
                    (int) (traceSize > 0 ? (limitedBufferSize / traceSize) : 0));
            mappedBuffer.buffer.order(ByteOrder.LITTLE_ENDIAN);
            return mappedBuffer;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        buffers.clear();
    }

    private static class MappedBuffer {
        private final ByteBuffer buffer;
        private final int firstTraceIndex;
        private final int numberOfTraces;

        MappedBuffer(ByteBuffer buffer, int firstTraceIndex, int numberOfTraces) {
            this.buffer = buffer;
            this.firstTraceIndex = firstTraceIndex;
            this.numberOfTraces = numberOfTraces;
        }

        public ByteBuffer getBuffer() {
            return buffer;
        }

        public int getFirstTraceIndex() {
            return firstTraceIndex;
        }

        public int getNumberOfTraces() {
            return numberOfTraces;
        }
    }
}
