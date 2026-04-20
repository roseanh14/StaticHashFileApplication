package Algorithm;

import Data.Block;
import Data.FileHeader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileStorage {
    private final String fileName;
    private final int primaryBlockCount;
    private final int blockFactor;
    private final int blockSize;
    private final RandomAccessFile file;

    public FileStorage(String fileName, int primaryBlockCount, int blockFactor) throws IOException {
        this.fileName = fileName;
        this.primaryBlockCount = primaryBlockCount;
        this.blockFactor = blockFactor;
        this.blockSize = BlockSerializer.getBlockSize(blockFactor);
        this.file = new RandomAccessFile(new File(fileName), "rw");
    }

    public String getFileName() {
        return fileName;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void writeHeader(FileHeader header) throws IOException {
        file.seek(0);
        file.write(BlockSerializer.headerToBytes(header, blockSize));
    }

    public FileHeader readHeader() throws IOException {
        file.seek(0);
        byte[] data = new byte[blockSize];
        file.readFully(data);
        return BlockSerializer.bytesToHeader(data);
    }

    public void writePrimaryBlock(int blockIndex, Block block) throws IOException {
        file.seek(getPrimaryBlockOffset(blockIndex));
        file.write(BlockSerializer.blockToBytes(block, blockFactor));
    }

    public Block readPrimaryBlock(int blockIndex) throws IOException {
        file.seek(getPrimaryBlockOffset(blockIndex));
        byte[] data = new byte[blockSize];
        file.readFully(data);
        return BlockSerializer.bytesToBlock(data, blockFactor);
    }

    public void writeBlockAtOffset(long offset, Block block) throws IOException {
        file.seek(offset);
        file.write(BlockSerializer.blockToBytes(block, blockFactor));
    }

    public Block readBlockAtOffset(long offset) throws IOException {
        file.seek(offset);
        byte[] data = new byte[blockSize];
        file.readFully(data);
        return BlockSerializer.bytesToBlock(data, blockFactor);
    }

    public long getOverflowBlockOffsetByIndex(int overflowBlockIndex) {
        return getOverflowBlockOffset(overflowBlockIndex);
    }

    public void close() throws IOException {
        file.close();
    }

    private long getPrimaryBlockOffset(int blockIndex) {
        return blockSize + (long) blockIndex * blockSize;
    }

    private long getOverflowBlockOffset(int overflowBlockIndex) {
        return blockSize + (long) primaryBlockCount * blockSize + (long) overflowBlockIndex * blockSize;
    }
}
