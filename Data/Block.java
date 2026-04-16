package Data;

public class Block {
    private int validRecordCount;
    private int nextOverflowBlockIndex;
    private MunicipalityRecord[] records;

    public Block(int blockFactor) {
        this.validRecordCount = 0;
        this.nextOverflowBlockIndex = -1;
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

    public int getNextOverflowBlockIndex() {
        return nextOverflowBlockIndex;
    }

    public void setNextOverflowBlockIndex(int nextOverflowBlockIndex) {
        this.nextOverflowBlockIndex = nextOverflowBlockIndex;
    }

    public MunicipalityRecord[] getRecords() {
        return records;
    }

    public void setRecords(MunicipalityRecord[] records) {
        this.records = records;
    }
}