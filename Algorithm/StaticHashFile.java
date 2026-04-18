package Algorithm;

import Data.Block;
import Data.FileHeader;
import Data.MunicipalityRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StaticHashFile {
    private final FileStorage storage;
    private final int primaryBlockCount;
    private final int blockFactor;
    private FileHeader header;

    public StaticHashFile(String fileName, int primaryBlockCount, int blockFactor) throws IOException {
        this.storage = new FileStorage(fileName, primaryBlockCount, blockFactor);
        this.primaryBlockCount = primaryBlockCount;
        this.blockFactor = blockFactor;

        initializeIfNeeded();
    }

    private void initializeIfNeeded() throws IOException {
        try {
            header = storage.readHeader();
        } catch (IOException exception) {
            header = new FileHeader(
                    primaryBlockCount,
                    blockFactor,
                    MunicipalityRecord.getByteSize(), //nahrada za MunicipalityRecord.BYTE_SIZE
                    storage.getBlockSize(),
                    0
            );

            storage.writeHeader(header);

            Block emptyBlock = new Block(blockFactor);
            for (int i = 0; i < primaryBlockCount; i++) {
                storage.writePrimaryBlock(i, emptyBlock);
            }
        }
    }

    public void insertAll(Set<MunicipalityRecord> records) throws IOException {
        for (MunicipalityRecord record : records) {
            insert(record);
        }
    }

    public boolean insert(MunicipalityRecord record) throws IOException {
        if (find(record.getName()) != null) {
            return false;
        }

        int primaryIndex = HashFunction.hash(record.getName(), primaryBlockCount);
        Block primaryBlock = storage.readPrimaryBlock(primaryIndex);

        if (addRecordToBlock(primaryBlock, record)) {
            storage.writePrimaryBlock(primaryIndex, primaryBlock);
            return true;
        }

        int overflowIndex = primaryBlock.getNextOverflowBlockIndex();

        if (overflowIndex == -1) {
            int newOverflowIndex = allocateOverflowBlock();
            primaryBlock.setNextOverflowBlockIndex(newOverflowIndex);
            storage.writePrimaryBlock(primaryIndex, primaryBlock);

            Block overflowBlock = storage.readOverflowBlock(newOverflowIndex);
            addRecordToBlock(overflowBlock, record);
            storage.writeOverflowBlock(newOverflowIndex, overflowBlock);
            return true;
        }

        while (true) {
            Block overflowBlock = storage.readOverflowBlock(overflowIndex);

            if (addRecordToBlock(overflowBlock, record)) {
                storage.writeOverflowBlock(overflowIndex, overflowBlock);
                return true;
            }

            if (overflowBlock.getNextOverflowBlockIndex() == -1) {
                int newOverflowIndex = allocateOverflowBlock();
                overflowBlock.setNextOverflowBlockIndex(newOverflowIndex);
                storage.writeOverflowBlock(overflowIndex, overflowBlock);

                Block newOverflowBlock = storage.readOverflowBlock(newOverflowIndex);
                addRecordToBlock(newOverflowBlock, record);
                storage.writeOverflowBlock(newOverflowIndex, newOverflowBlock);
                return true;
            }

            overflowIndex = overflowBlock.getNextOverflowBlockIndex();
        }
    }

    public MunicipalityRecord find(String key) throws IOException {
        int primaryIndex = HashFunction.hash(key, primaryBlockCount);
        Block primaryBlock = storage.readPrimaryBlock(primaryIndex);

        MunicipalityRecord found = findInBlock(primaryBlock, key);
        if (found != null) {
            return found;
        }

        int overflowIndex = primaryBlock.getNextOverflowBlockIndex();

        while (overflowIndex != -1) {
            Block overflowBlock = storage.readOverflowBlock(overflowIndex);
            found = findInBlock(overflowBlock, key);

            if (found != null) {
                return found;
            }

            overflowIndex = overflowBlock.getNextOverflowBlockIndex();
        }

        return null;
    }

    public boolean delete(String key) throws IOException {
        int primaryIndex = HashFunction.hash(key, primaryBlockCount);
        Block primaryBlock = storage.readPrimaryBlock(primaryIndex);

        if (deleteFromBlock(primaryBlock, key)) {
            storage.writePrimaryBlock(primaryIndex, primaryBlock);
            return true;
        }

        int overflowIndex = primaryBlock.getNextOverflowBlockIndex();

        while (overflowIndex != -1) {
            Block overflowBlock = storage.readOverflowBlock(overflowIndex);

            if (deleteFromBlock(overflowBlock, key)) {
                storage.writeOverflowBlock(overflowIndex, overflowBlock);
                return true;
            }

            overflowIndex = overflowBlock.getNextOverflowBlockIndex();
        }

        return false;
    }

    public List<MunicipalityRecord> getAllRecords() throws IOException {
        List<MunicipalityRecord> result = new ArrayList<>();

        for (int i = 0; i < primaryBlockCount; i++) {
            Block primaryBlock = storage.readPrimaryBlock(i);
            collectActiveRecords(primaryBlock, result);

            int overflowIndex = primaryBlock.getNextOverflowBlockIndex();
            while (overflowIndex != -1) {
                Block overflowBlock = storage.readOverflowBlock(overflowIndex);
                collectActiveRecords(overflowBlock, result);
                overflowIndex = overflowBlock.getNextOverflowBlockIndex();
            }
        }

        return result;
    }

    public int countAllRecords() throws IOException {
        int count = 0;

        for (int i = 0; i < primaryBlockCount; i++) {
            Block primaryBlock = storage.readPrimaryBlock(i);
            count += countActiveInBlock(primaryBlock);

            int overflowIndex = primaryBlock.getNextOverflowBlockIndex();
            while (overflowIndex != -1) {
                Block overflowBlock = storage.readOverflowBlock(overflowIndex);
                count += countActiveInBlock(overflowBlock);
                overflowIndex = overflowBlock.getNextOverflowBlockIndex();
            }
        }

        return count;
    }

    public FileHeader getFileHeader() throws IOException {
        header = storage.readHeader();
        return header;
    }

    public String getFileName() {
        return storage.getFileName();
    }

    public void close() throws IOException {
        storage.close();
    }

    private boolean addRecordToBlock(Block block, MunicipalityRecord record) {
        MunicipalityRecord[] records = block.getRecords();

        for (int i = 0; i < records.length; i++) {
            if (!records[i].isActive()) {
                records[i] = record;
                block.setValidRecordCount(block.getValidRecordCount() + 1);
                return true;
            }
        }

        return false;
    }

    private MunicipalityRecord findInBlock(Block block, String key) {
        for (MunicipalityRecord record : block.getRecords()) {
            if (record.isActive() && record.getName().equals(key)) {
                return record;
            }
        }

        return null;
    }

    private boolean deleteFromBlock(Block block, String key) {
        for (MunicipalityRecord record : block.getRecords()) {
            if (record.isActive() && record.getName().equals(key)) {
                record.markDeleted();
                block.setValidRecordCount(block.getValidRecordCount() - 1);
                return true;
            }
        }

        return false;
    }

    private void collectActiveRecords(Block block, List<MunicipalityRecord> result) {
        for (MunicipalityRecord record : block.getRecords()) {
            if (record.isActive()) {
                result.add(record);
            }
        }
    }

    private int countActiveInBlock(Block block) {
        int count = 0;

        for (MunicipalityRecord record : block.getRecords()) {
            if (record.isActive()) {
                count++;
            }
        }

        return count;
    }

    private int allocateOverflowBlock() throws IOException {
        int newOverflowBlockIndex = header.getOverflowBlockCount();
        Block newBlock = new Block(blockFactor);

        storage.writeOverflowBlock(newOverflowBlockIndex, newBlock);

        header.setOverflowBlockCount(newOverflowBlockIndex + 1);
        storage.writeHeader(header);

        return newOverflowBlockIndex;
    }
}