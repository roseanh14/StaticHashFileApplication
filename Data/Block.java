package Data;

public class Block {
    private int validRecordCount;
    private long nextOverflowBlockOffset;
    private MunicipalityRecord[] records;

    public Block(int blockFactor) {
        this.validRecordCount = 0;
        this.nextOverflowBlockOffset = -1;
        this.records = new MunicipalityRecord[blockFactor];

        for (int i = 0; i < blockFactor; i++) {
            records[i] = new MunicipalityRecord();
        }
    }

    public int getValidRecordCount() {
        return validRecordCount;
    }

    public void setValidRecordCount(int validRecordCount) {
        this.validRecordCount = validRecordCount;
    }

    public long getNextOverflowBlockOffset() {
        return nextOverflowBlockOffset;
    }

    public void setNextOverflowBlockOffset(long nextOverflowBlockOffset) {
        this.nextOverflowBlockOffset = nextOverflowBlockOffset;
    }

    public MunicipalityRecord[] getRecords() {
        return records;
    }

    public void setRecords(MunicipalityRecord[] records) {
        this.records = records;
    }
}