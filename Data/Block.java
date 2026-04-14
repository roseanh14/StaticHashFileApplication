package Data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Block {
    private final int blockFactor;
    private int validRecordCount;
    private int nextOverflowBlockIndex;
    private final MunicipalityRecord[] records;

    public Block(int blockFactor) {
        this.blockFactor = blockFactor;
        this.validRecordCount = 0;
        this.nextOverflowBlockIndex = -1;
        this.records = new MunicipalityRecord[blockFactor];

        for (int i = 0; i < blockFactor; i++) {
            records[i] = new MunicipalityRecord();
        }
    }

    public int getNextOverflowBlockIndex() {
        return nextOverflowBlockIndex;
    }

    public void setNextOverflowBlockIndex(int nextOverflowBlockIndex) {
        this.nextOverflowBlockIndex = nextOverflowBlockIndex;
    }

    public MunicipalityRecord[] getRecords() {
        return records;
    }

    public boolean addRecord(MunicipalityRecord newRecord) {
        for (int i = 0; i < blockFactor; i++) {
            if (!records[i].isActive()) {
                records[i] = new MunicipalityRecord(
                        newRecord.getName(),
                        newRecord.getPopulation(),
                        newRecord.getAltitude()
                );
                validRecordCount++;
                return true;
            }
        }

        return false;
    }

    public MunicipalityRecord findRecord(String name) {
        for (MunicipalityRecord record : records) {
            if (record.isActive() && record.getName().equals(name)) {
                return record;
            }
        }

        return null;
    }

    public boolean deleteRecord(String name) {
        for (MunicipalityRecord record : records) {
            if (record.isActive() && record.getName().equals(name)) {
                record.markDeleted();
                validRecordCount--;
                return true;
            }
        }

        return false;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(byteStream);

        output.writeInt(validRecordCount);
        output.writeInt(nextOverflowBlockIndex);

        for (MunicipalityRecord record : records) {
            output.write(record.toByteArray());
        }

        return byteStream.toByteArray();
    }

    public static Block fromByteArray(byte[] data, int blockFactor) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        DataInputStream input = new DataInputStream(byteStream);

        Block block = new Block(blockFactor);
        block.validRecordCount = input.readInt();
        block.nextOverflowBlockIndex = input.readInt();

        for (int i = 0; i < blockFactor; i++) {
            byte[] recordBytes = new byte[MunicipalityRecord.BYTE_SIZE];
            input.readFully(recordBytes);
            block.records[i] = MunicipalityRecord.fromByteArray(recordBytes);
        }

        return block;
    }

    public static int getByteSize(int blockFactor) {
        return Integer.BYTES + Integer.BYTES + blockFactor * MunicipalityRecord.BYTE_SIZE;
    }
}