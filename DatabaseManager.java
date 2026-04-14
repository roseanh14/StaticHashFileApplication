import Algorithm.StaticHashFile;
import Data.MunicipalityRecord;
import GUI.RecordTableFrame;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class DatabaseManager implements RecordTableFrame.DatabaseManagerBridge {
    private static final String FILE_NAME = "municipality_hash.dat";
    private static final int PRIMARY_BLOCK_COUNT = 2000;
    private static final int BLOCK_FACTOR = 3;

    private static final String[] NAME_PREFIXES = {
            "North", "South", "East", "West", "Upper", "Lower", "New", "Old",
            "Grand", "Little", "Silver", "Golden", "Bright", "Green", "White",
            "Black", "Red", "Blue", "Clear", "High", "Low", "Stone", "River", "Lake",
            "Oak", "Pine", "Elm", "Ash", "Maple", "Cedar", "Iron", "Willow"
    };

    private static final String[] NAME_ROOTS = {
            "Hill", "Brook", "Valley", "Field", "Wood", "Bridge", "Creek", "Lake",
            "Ford", "Grove", "Point", "Meadow", "Spring", "Ridge", "Haven",
            "Cliff", "Falls", "Cross", "Castle", "Heath", "Pine", "Oak", "Elm", "Ash",
            "Stone", "River", "Harbor", "Mill", "Park", "View", "Gate", "Plain"
    };

    private static final String[] LOCATION_SUFFIXES = {
            "Village", "Town", "Heights", "Park", "Crossing", "Harbor", "View", "Grove",
            "Hollow", "Vale", "Ridge", "Point", "Field", "Falls", "Bridge", "Meadow"
    };

    private static final String[] EXTRA_SUFFIXES = {
            "", "", "", "", "North", "South", "East", "West",
            "Center", "Mill", "Gate", "Bridge", "Side", "Point", "Heights", "Cross"
    };

    private final StaticHashFile hashFile;
    private final Random random;

    public DatabaseManager() throws IOException {
        File file = new File(FILE_NAME);
        if (file.exists() && !file.delete()) {
            throw new IOException("Unable to delete existing file.");
        }

        this.hashFile = new StaticHashFile(FILE_NAME, PRIMARY_BLOCK_COUNT, BLOCK_FACTOR);
        this.random = new Random();
    }

    public void generateData(int count) throws IOException {
        Set<String> usedNames = new HashSet<>();

        while (usedNames.size() < count) {
            MunicipalityRecord record = generateRandomRecord();
            if (usedNames.add(record.getName())) {
                hashFile.insert(record);
            }
        }
    }

    private MunicipalityRecord generateRandomRecord() {
        String prefix = NAME_PREFIXES[random.nextInt(NAME_PREFIXES.length)];
        String root = NAME_ROOTS[random.nextInt(NAME_ROOTS.length)];
        String locationSuffix = LOCATION_SUFFIXES[random.nextInt(LOCATION_SUFFIXES.length)];
        String extraSuffix = EXTRA_SUFFIXES[random.nextInt(EXTRA_SUFFIXES.length)];

        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(prefix).append(" ").append(root).append(" ").append(locationSuffix);

        if (!extraSuffix.isEmpty()) {
            nameBuilder.append(" ").append(extraSuffix);
        }

        String name = nameBuilder.toString();
        int population = 500 + random.nextInt(150001);
        int altitude = 100 + random.nextInt(1401);

        return new MunicipalityRecord(name, population, altitude);
    }

    @Override
    public boolean insert(String name, int population, int altitude) throws IOException {
        return hashFile.insert(new MunicipalityRecord(name, population, altitude));
    }

    @Override
    public MunicipalityRecord find(String name) throws IOException {
        return hashFile.find(name);
    }

    @Override
    public boolean delete(String name) throws IOException {
        return hashFile.delete(name);
    }

    @Override
    public List<MunicipalityRecord> getAllRecords() throws IOException {
        return hashFile.getAllRecords();
    }

    public int countAllRecords() throws IOException {
        return hashFile.countAllRecords();
    }

    @Override
    public void printAll() throws IOException {
        hashFile.printAll();
    }

    @Override
    public void printInfo() throws IOException {
        hashFile.printFileInfo();
    }

    public void close() throws IOException {
        hashFile.close();
    }
}