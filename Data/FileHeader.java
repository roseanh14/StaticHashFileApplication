package Data;

public class FileHeader {
    private int primaryBlockCount;
    private int blockFactor;
    private int recordSize;
    private int blockSize;
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
}