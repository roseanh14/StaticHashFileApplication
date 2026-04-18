package Algorithm;

import Data.Block;
import Data.FileHeader;
import Data.MunicipalityRecord;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class BlockSerializer {
    private BlockSerializer() {
    }

    public static byte[] municipalityToBytes(MunicipalityRecord record) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(byteStream);

        String fixedName = formatName(record.getName());
        for (int i = 0; i < fixedName.length(); i++) {
            output.writeChar(fixedName.charAt(i));
        }

        output.writeInt(record.getPopulation());
        output.writeInt(record.getAltitude());
        output.writeByte(record.getStatus());

        return byteStream.toByteArray();
    }

    public static MunicipalityRecord bytesToMunicipality(byte[] data) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        DataInputStream input = new DataInputStream(byteStream);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < MunicipalityRecord.getNameLength(); i++) { //nahrada volani public getteru za MunicipalityRecord.NAME_LENGTH
            builder.append(input.readChar());
        }

        /*
        staci dat jen do jednoho radku, puvodni kod nechavam zakomentovany
        MunicipalityRecord record;
        record = new MunicipalityRecord(builder.toString().trim(), input.readInt(), input.readInt());
        */
        MunicipalityRecord record = new MunicipalityRecord(builder.toString().trim(), input.readInt(), input.readInt());
        record.setStatus(input.readByte());

        return record;
    }

    public static byte[] blockToBytes(Block block, int blockFactor) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(byteStream);

        output.writeInt(block.getValidRecordCount());
        output.writeInt(block.getNextOverflowBlockIndex());

        for (int i = 0; i < blockFactor; i++) {
            output.write(municipalityToBytes(block.getRecords()[i]));
        }

        return byteStream.toByteArray();
    }

    public static Block bytesToBlock(byte[] data, int blockFactor) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        DataInputStream input = new DataInputStream(byteStream);

        Block block = new Block(blockFactor);
        block.setValidRecordCount(input.readInt());
        block.setNextOverflowBlockIndex(input.readInt());

        MunicipalityRecord[] records = new MunicipalityRecord[blockFactor];
        for (int i = 0; i < blockFactor; i++) {
            byte[] recordBytes = new byte[MunicipalityRecord.getByteSize()]; //nahrada za MunicipalityRecord.BYTE_SIZE
            input.readFully(recordBytes);
            records[i] = bytesToMunicipality(recordBytes);
        }

        block.setRecords(records);
        return block;
    }

    public static byte[] headerToBytes(FileHeader header, int targetSize) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(byteStream);

        output.writeInt(header.getPrimaryBlockCount());
        output.writeInt(header.getBlockFactor());
        output.writeInt(header.getRecordSize());
        output.writeInt(header.getBlockSize());
        output.writeInt(header.getOverflowBlockCount());

        while (byteStream.size() < targetSize) {
            output.writeByte(0);
        }

        return byteStream.toByteArray();
    }

    public static FileHeader bytesToHeader(byte[] data) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        DataInputStream input = new DataInputStream(byteStream);

        int primaryBlockCount = input.readInt();
        int blockFactor = input.readInt();
        int recordSize = input.readInt();
        int blockSize = input.readInt();
        int overflowBlockCount = input.readInt();

        return new FileHeader(primaryBlockCount, blockFactor, recordSize, blockSize, overflowBlockCount);
    }

    public static int getBlockSize(int blockFactor) {
        return Integer.BYTES + Integer.BYTES + blockFactor * MunicipalityRecord.getByteSize(); //nahrada za MunicipalityRecord.BYTE_SIZE
    }

    private static String formatName(String value) {
        String text = value == null ? "" : value;

        if (text.length() > MunicipalityRecord.getNameLength()) { //nahrada za MunicipalityRecord.NAME_LENGTH
            return text.substring(0, MunicipalityRecord.getNameLength()); //nahrada za MunicipalityRecord.NAME_LENGTH
        }

        StringBuilder builder = new StringBuilder(text);
        while (builder.length() < MunicipalityRecord.getNameLength()) { //nahrada za MunicipalityRecord.NAME_LENGTH
            builder.append(' ');
        }

        return builder.toString();
    }
}