package Data;

public class MunicipalityRecord {
    /*
    tohle by melo byt vse private, u EMPTY, ACTIVE a DELETED je to dokonce jasne, jelikoz se volaji pouze v ramci teto tridy
    u NAME_LENGTH a BYTE_SIZE je potreba to vyresit pres public staticky getter ktery se bude volat vne,
    puvodni kod nechavam zakomentovany, pridavam svoji upravu

    public static final int NAME_LENGTH = 40;
    public static final byte EMPTY = 0;
    public static final byte ACTIVE = 1;
    public static final byte DELETED = 2;
    public static final int BYTE_SIZE = NAME_LENGTH * 2 + Integer.BYTES + Integer.BYTES + 1;
    */
    private static final int NAME_LENGTH = 40;
    private static final byte EMPTY = 0;
    private static final byte ACTIVE = 1;
    private static final byte DELETED = 2;
    private static final int BYTE_SIZE = NAME_LENGTH * 2 + Integer.BYTES + Integer.BYTES + 1;

    private final String name;
    private final int population;
    private final int altitude;
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

    //pridani getter metody pro pristup k NAME_LENGTH
    public static int getNameLength() { return NAME_LENGTH; }

    //pridani getter metody pro pristup k BYTE_SIZE
    public static int getByteSize() { return BYTE_SIZE; }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public int getAltitude() {
        return altitude;
    }

    public byte getStatus() {
        return status;
    }

    public boolean isActive() {
        return status == ACTIVE;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public void markDeleted() {
        this.status = DELETED;
    }

    @Override
    public String toString() {
        return name + " | Population: " + population + " | Altitude: " + altitude + " m";
    }
}