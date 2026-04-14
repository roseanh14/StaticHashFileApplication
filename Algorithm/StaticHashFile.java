package Algorithm;

import Data.Block;
import Data.FileHeader;
import Data.MunicipalityRecord;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class StaticHashFile {
    private final String fileName;
    private final int primaryBlockCount;
    private final int blockFactor;
    private final int blockSize;
    private final RandomAccessFile file;
    private FileHeader header;

    public StaticHashFile(String fileName, int primaryBlockCount, int blockFactor) throws IOException {
        this.fileName = fileName;
        this.primaryBlockCount = primaryBlockCount;
        this.blockFactor = blockFactor;
        this.blockSize = Block.getByteSize(blockFactor);
        this.file = new RandomAccessFile(new File(fileName), "rw");

        if (file.length() == 0) {
            initializeFile();
        } else {
            this.header = readHeader();
        }
    }

    private void initializeFile() throws IOException {
        this.header = new FileHeader(
                primaryBlockCount,
                blockFactor,
                MunicipalityRecord.BYTE_SIZE,
                blockSize,
                0
        );

        writeHeader();

        Block emptyBlock = new Block(blockFactor);
        for (int i = 0; i < primaryBlockCount; i++) {
            writePrimaryBlock(i, emptyBlock);
        }
    }

    private FileHeader readHeader() throws IOException {
        file.seek(0);
        byte[] data = new byte[blockSize];
        file.readFully(data);
        return FileHeader.fromByteArray(data);
    }

    private void writeHeader() throws IOException {
        file.seek(0);
        file.write(header.toByteArray(blockSize));
    }

    private long getPrimaryBlockOffset(int blockIndex) {
        return blockSize + (long) blockIndex * blockSize;
    }

    private long getOverflowBlockOffset(int overflowBlockIndex) {
        return blockSize + (long) primaryBlockCount * blockSize + (long) overflowBlockIndex * blockSize;
    }

    private Block readPrimaryBlock(int blockIndex) throws IOException {
        file.seek(getPrimaryBlockOffset(blockIndex));
        byte[] data = new byte[blockSize];
        file.readFully(data);
        return Block.fromByteArray(data, blockFactor);
    }

    private void writePrimaryBlock(int blockIndex, Block block) throws IOException {
        file.seek(getPrimaryBlockOffset(blockIndex));
        file.write(block.toByteArray());
    }

    private Block readOverflowBlock(int overflowBlockIndex) throws IOException {
        file.seek(getOverflowBlockOffset(overflowBlockIndex));
        byte[] data = new byte[blockSize];
        file.readFully(data);
        return Block.fromByteArray(data, blockFactor);
    }

    private void writeOverflowBlock(int overflowBlockIndex, Block block) throws IOException {
        file.seek(getOverflowBlockOffset(overflowBlockIndex));
        file.write(block.toByteArray());
    }

    private int allocateOverflowBlock() throws IOException {
        int newOverflowBlockIndex = header.getOverflowBlockCount();
        Block newBlock = new Block(blockFactor);

        writeOverflowBlock(newOverflowBlockIndex, newBlock);

        header.setOverflowBlockCount(newOverflowBlockIndex + 1);
        writeHeader();

        return newOverflowBlockIndex;
    }

    public boolean insert(MunicipalityRecord record) throws IOException {
        if (find(record.getName()) != null) {
            return false;
        }

        int primaryIndex = HashFunction.hash(record.getName(), primaryBlockCount);
        Block primaryBlock = readPrimaryBlock(primaryIndex);

        if (primaryBlock.addRecord(record)) {
            writePrimaryBlock(primaryIndex, primaryBlock);
            return true;
        }

        int overflowIndex = primaryBlock.getNextOverflowBlockIndex();

        if (overflowIndex == -1) {
            int newOverflowIndex = allocateOverflowBlock();
            primaryBlock.setNextOverflowBlockIndex(newOverflowIndex);
            writePrimaryBlock(primaryIndex, primaryBlock);

            Block overflowBlock = readOverflowBlock(newOverflowIndex);
            overflowBlock.addRecord(record);
            writeOverflowBlock(newOverflowIndex, overflowBlock);
            return true;
        }

        while (true) {
            Block overflowBlock = readOverflowBlock(overflowIndex);

            if (overflowBlock.addRecord(record)) {
                writeOverflowBlock(overflowIndex, overflowBlock);
                return true;
            }

            if (overflowBlock.getNextOverflowBlockIndex() == -1) {
                int newOverflowIndex = allocateOverflowBlock();
                overflowBlock.setNextOverflowBlockIndex(newOverflowIndex);
                writeOverflowBlock(overflowIndex, overflowBlock);

                Block newOverflowBlock = readOverflowBlock(newOverflowIndex);
                newOverflowBlock.addRecord(record);
                writeOverflowBlock(newOverflowIndex, newOverflowBlock);
                return true;
            }

            overflowIndex = overflowBlock.getNextOverflowBlockIndex();
        }
    }

    public MunicipalityRecord find(String name) throws IOException {
        int primaryIndex = HashFunction.hash(name, primaryBlockCount);
        Block primaryBlock = readPrimaryBlock(primaryIndex);

        MunicipalityRecord found = primaryBlock.findRecord(name);
        if (found != null) {
            return found;
        }

        int overflowIndex = primaryBlock.getNextOverflowBlockIndex();

        while (overflowIndex != -1) {
            Block overflowBlock = readOverflowBlock(overflowIndex);
            found = overflowBlock.findRecord(name);

            if (found != null) {
                return found;
            }

            overflowIndex = overflowBlock.getNextOverflowBlockIndex();
        }

        return null;
    }

    public boolean delete(String name) throws IOException {
        int primaryIndex = HashFunction.hash(name, primaryBlockCount);
        Block primaryBlock = readPrimaryBlock(primaryIndex);

        if (primaryBlock.deleteRecord(name)) {
            writePrimaryBlock(primaryIndex, primaryBlock);
            return true;
        }

        int overflowIndex = primaryBlock.getNextOverflowBlockIndex();

        while (overflowIndex != -1) {
            Block overflowBlock = readOverflowBlock(overflowIndex);

            if (overflowBlock.deleteRecord(name)) {
                writeOverflowBlock(overflowIndex, overflowBlock);
                return true;
            }

            overflowIndex = overflowBlock.getNextOverflowBlockIndex();
        }

        return false;
    }

    public List<MunicipalityRecord> getAllRecords() throws IOException {
        List<MunicipalityRecord> result = new ArrayList<>();

        for (int i = 0; i < primaryBlockCount; i++) {
            Block primaryBlock = readPrimaryBlock(i);
            collectRecords(primaryBlock, result);

            int overflowIndex = primaryBlock.getNextOverflowBlockIndex();
            while (overflowIndex != -1) {
                Block overflowBlock = readOverflowBlock(overflowIndex);
                collectRecords(overflowBlock, result);
                overflowIndex = overflowBlock.getNextOverflowBlockIndex();
            }
        }

        return result;
    }

    public int countAllRecords() throws IOException {
        int count = 0;

        for (int i = 0; i < primaryBlockCount; i++) {
            Block primaryBlock = readPrimaryBlock(i);
            count += countRecordsInBlock(primaryBlock);

            int overflowIndex = primaryBlock.getNextOverflowBlockIndex();
            while (overflowIndex != -1) {
                Block overflowBlock = readOverflowBlock(overflowIndex);
                count += countRecordsInBlock(overflowBlock);
                overflowIndex = overflowBlock.getNextOverflowBlockIndex();
            }
        }

        return count;
    }

    public void printAll() throws IOException {
        for (MunicipalityRecord record : getAllRecords()) {
            System.out.println(record);
        }
    }

    private void collectRecords(Block block, List<MunicipalityRecord> result) {
        for (MunicipalityRecord record : block.getRecords()) {
            if (record.isActive()) {
                result.add(record);
            }
        }
    }

    private int countRecordsInBlock(Block block) {
        int count = 0;

        for (MunicipalityRecord record : block.getRecords()) {
            if (record.isActive()) {
                count++;
            }
        }

        return count;
    }

    public void printFileInfo() throws IOException {
        header = readHeader();

        System.out.println("File name: " + fileName);
        System.out.println("Primary block count: " + header.getPrimaryBlockCount());
        System.out.println("Block factor: " + header.getBlockFactor());
        System.out.println("Record size: " + header.getRecordSize() + " bytes");
        System.out.println("Block size: " + header.getBlockSize() + " bytes");
        System.out.println("Overflow block count: " + header.getOverflowBlockCount());
        System.out.println("File size: " + file.length() + " bytes");
    }

    public void close() throws IOException {
        file.close();
    }
}