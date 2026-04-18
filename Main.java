import Algorithm.RecordGenerator;
import Algorithm.StaticHashFile;
import Data.MunicipalityRecord;
import GUI.RecordTableFrame;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class Main {
    private static final String FILE_NAME = "municipality_hash.dat";
    private static final int PRIMARY_BLOCK_COUNT = 2000;
    private static final int BLOCK_FACTOR = 3;

    public static void main(String[] args) {
        try {
            File file = new File(FILE_NAME);
            if (file.exists() && !file.delete()) {
                throw new IOException("Unable to delete existing file.");
            }

            StaticHashFile hashFile = new StaticHashFile(FILE_NAME, PRIMARY_BLOCK_COUNT, BLOCK_FACTOR);

            RecordGenerator generator = new RecordGenerator();
            Set<MunicipalityRecord> records = generator.generateUniqueRecords(10); //zmena na generovani pouze 10ti zaznamu kvuli testovani
            hashFile.insertAll(records);

            System.out.println("Generated record count: " + hashFile.countAllRecords());

            RecordTableFrame frame = new RecordTableFrame(hashFile);
            frame.setVisible(true);
        } catch (IOException exception) {
            System.out.println("File error: " + exception.getMessage());
        }
    }
}