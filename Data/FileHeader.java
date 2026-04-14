package Data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FileHeader {
    private final int primaryBlockCount;
    private final int blockFactor;
    private final int recordSize;
    private final int blockSize;
    private int overflowBlockCount;

    public FileHeader(int primaryBlockCount, int blockFactor, int recordSize, int blockSize, int overflowBlockCount) {
        this.primaryBlockCount = primaryBlockCount;
        this.blockFactor = blockFactor;
        this.recordSize = recordSize;
        this.blockSize = blockSize;
        this.overflowBlockCount = overflowBlockCount;
    }

    public int getPrimaryBlockCount() {
        return primaryBlockCount;
    }

    public int getBlockFactor() {
        return blockFactor;
    }

    public int getRecordSize() {
        return recordSize;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int getOverflowBlockCount() {
        return overflowBlockCount;
    }

    public void setOverflowBlockCount(int overflowBlockCount) {
        this.overflowBlockCount = overflowBlockCount;
    }

    public byte[] toByteArray(int targetSize) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(byteStream);

        output.writeInt(primaryBlockCount);
        output.writeInt(blockFactor);
        output.writeInt(recordSize);
        output.writeInt(blockSize);
        output.writeInt(overflowBlockCount);

        while (byteStream.size() < targetSize) {
            output.writeByte(0);
        }

        return byteStream.toByteArray();
    }

    public static FileHeader fromByteArray(byte[] data) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        DataInputStream input = new DataInputStream(byteStream);

        int primaryBlockCount = input.readInt();
        int blockFactor = input.readInt();
        int recordSize = input.readInt();
        int blockSize = input.readInt();
        int overflowBlockCount = input.readInt();

        return new FileHeader(primaryBlockCount, blockFactor, recordSize, blockSize, overflowBlockCount);
    }
}