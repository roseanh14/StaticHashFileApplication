import GUI.RecordTableFrame;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseManager manager = new DatabaseManager();

            System.out.println("Generating 10000 random records...");
            manager.generateData(10000);

            System.out.println("Generated record count: " + manager.countAllRecords());
            manager.printInfo();

            RecordTableFrame frame = new RecordTableFrame(manager);
            frame.setVisible(true);
        } catch (IOException exception) {
            System.out.println("File error: " + exception.getMessage());
        }
    }
}