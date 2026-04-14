package Data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MunicipalityRecord {
    public static final int NAME_LENGTH = 40;
    public static final byte EMPTY = 0;
    public static final byte ACTIVE = 1;
    public static final byte DELETED = 2;
    public static final int BYTE_SIZE = NAME_LENGTH * 2 + Integer.BYTES + Integer.BYTES + 1;

    private String name;
    private int population;
    private int altitude;
    private byte status;

    public MunicipalityRecord() {
        this.name = "";
        this.population = 0;
        this.altitude = 0;
        this.status = EMPTY;
    }

    public MunicipalityRecord(String name, int population, int altitude) {
        this.name = name;
        this.population = population;
        this.altitude = altitude;
        this.status = ACTIVE;
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public int getAltitude() {
        return altitude;
    }

    public boolean isActive() {
        return status == ACTIVE;
    }

    public void markDeleted() {
        status = DELETED;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(byteStream);

        String fixedName = formatName(name);
        for (int i = 0; i < fixedName.length(); i++) {
            output.writeChar(fixedName.charAt(i));
        }

        output.writeInt(population);
        output.writeInt(altitude);
        output.writeByte(status);

        return byteStream.toByteArray();
    }

    public static MunicipalityRecord fromByteArray(byte[] data) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        DataInputStream input = new DataInputStream(byteStream);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < NAME_LENGTH; i++) {
            builder.append(input.readChar());
        }

        MunicipalityRecord record = new MunicipalityRecord();
        record.name = builder.toString().trim();
        record.population = input.readInt();
        record.altitude = input.readInt();
        record.status = input.readByte();

        return record;
    }

    private static String formatName(String value) {
        String text = value == null ? "" : value;

        if (text.length() > NAME_LENGTH) {
            return text.substring(0, NAME_LENGTH);
        }

        StringBuilder builder = new StringBuilder(text);
        while (builder.length() < NAME_LENGTH) {
            builder.append(' ');
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return name + " | Population: " + population + " | Altitude: " + altitude + " m";
    }
}