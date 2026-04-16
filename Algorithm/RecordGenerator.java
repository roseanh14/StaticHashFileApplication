package Algorithm;

import Data.MunicipalityRecord;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RecordGenerator {
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

    private final Random random;

    public RecordGenerator() {
        this.random = new Random();
    }

    public MunicipalityRecord generateRandomRecord() {
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

    public Set<MunicipalityRecord> generateUniqueRecords(int count) {
        Set<String> usedNames = new HashSet<>();
        Set<MunicipalityRecord> records = new HashSet<>();

        while (usedNames.size() < count) {
            MunicipalityRecord record = generateRandomRecord();
            if (usedNames.add(record.getName())) {
                records.add(record);
            }
        }

        return records;
    }
}