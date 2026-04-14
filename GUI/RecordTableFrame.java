package GUI;

import Data.MunicipalityRecord;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

public class RecordTableFrame extends JFrame {
    private final DatabaseManagerBridge manager;
    private final DefaultTableModel model;
    private final JTextField searchField;
    private final JTextField insertNameField;
    private final JTextField insertPopulationField;
    private final JTextField insertAltitudeField;
    private final JTextField deleteField;

    public RecordTableFrame(DatabaseManagerBridge manager) throws IOException {
        this.manager = manager;

        setTitle("Static Hash File Application");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    RecordTableFrame.this.manager.close();
                } catch (IOException exception) {
                    JOptionPane.showMessageDialog(
                            null,
                            exception.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        model = new DefaultTableModel(new Object[]{"Municipality", "Population", "Altitude"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        searchField = new JTextField();
        insertNameField = new JTextField();
        insertPopulationField = new JTextField();
        insertAltitudeField = new JTextField();
        deleteField = new JTextField();

        add(createTopPanel(), BorderLayout.NORTH);
        add(new JScrollPane(createTable()), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        refreshTable();
    }

    private JTable createTable() {
        return new JTable(model);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel actionPanel = new JPanel(new GridLayout(3, 1, 0, 8));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Municipality"));
        searchPanel.add(searchField, BorderLayout.CENTER);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchRecord());
        searchPanel.add(searchButton, BorderLayout.EAST);

        JPanel insertPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        insertPanel.setBorder(BorderFactory.createTitledBorder("Insert Municipality"));
        insertPanel.add(insertNameField);
        insertPanel.add(insertPopulationField);
        insertPanel.add(insertAltitudeField);

        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(e -> insertRecord());
        insertPanel.add(insertButton);

        insertNameField.setToolTipText("Municipality name");
        insertPopulationField.setToolTipText("Population");
        insertAltitudeField.setToolTipText("Altitude");

        JPanel deletePanel = new JPanel(new BorderLayout(8, 0));
        deletePanel.setBorder(BorderFactory.createTitledBorder("Delete Municipality"));
        deletePanel.add(deleteField, BorderLayout.CENTER);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteRecord());
        deletePanel.add(deleteButton, BorderLayout.EAST);

        actionPanel.add(searchPanel);
        actionPanel.add(insertPanel);
        actionPanel.add(deletePanel);

        topPanel.add(actionPanel, BorderLayout.CENTER);
        return topPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            try {
                refreshTable();
            } catch (IOException exception) {
                showError(exception.getMessage());
            }
        });

        JButton printAllButton = new JButton("Print All To Console");
        printAllButton.addActionListener(e -> {
            try {
                manager.printAll();
                JOptionPane.showMessageDialog(this, "All records were printed to the console.");
            } catch (IOException exception) {
                showError(exception.getMessage());
            }
        });

        JButton fileInfoButton = new JButton("Show File Info");
        fileInfoButton.addActionListener(e -> {
            try {
                manager.printInfo();
                JOptionPane.showMessageDialog(this, "File info was printed to the console.");
            } catch (IOException exception) {
                showError(exception.getMessage());
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        buttonPanel.add(refreshButton);
        buttonPanel.add(printAllButton);
        buttonPanel.add(fileInfoButton);

        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        return bottomPanel;
    }

    private void refreshTable() throws IOException {
        model.setRowCount(0);

        List<MunicipalityRecord> records = manager.getAllRecords();
        for (MunicipalityRecord record : records) {
            model.addRow(new Object[]{
                    record.getName(),
                    record.getPopulation(),
                    record.getAltitude()
            });
        }

        setTitle("Static Hash File Application - Records: " + records.size());
    }

    private void searchRecord() {
        String name = searchField.getText().trim();

        if (name.isEmpty()) {
            showError("Enter a municipality name.");
            return;
        }

        try {
            MunicipalityRecord record = manager.find(name);

            if (record == null) {
                JOptionPane.showMessageDialog(this, "Record not found.");
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Municipality: " + record.getName()
                                + "\nPopulation: " + record.getPopulation()
                                + "\nAltitude: " + record.getAltitude() + " m"
                );
            }
        } catch (IOException exception) {
            showError(exception.getMessage());
        }
    }

    private void insertRecord() {
        String name = insertNameField.getText().trim();
        String populationText = insertPopulationField.getText().trim();
        String altitudeText = insertAltitudeField.getText().trim();

        if (name.isEmpty() || populationText.isEmpty() || altitudeText.isEmpty()) {
            showError("Fill in all insert fields.");
            return;
        }

        try {
            int population = Integer.parseInt(populationText);
            int altitude = Integer.parseInt(altitudeText);

            boolean inserted = manager.insert(name, population, altitude);

            if (!inserted) {
                JOptionPane.showMessageDialog(this, "Record already exists.");
                return;
            }

            refreshTable();
            insertNameField.setText("");
            insertPopulationField.setText("");
            insertAltitudeField.setText("");
            JOptionPane.showMessageDialog(this, "Record inserted.");
        } catch (NumberFormatException exception) {
            showError("Population and altitude must be integers.");
        } catch (IOException exception) {
            showError(exception.getMessage());
        }
    }

    private void deleteRecord() {
        String name = deleteField.getText().trim();

        if (name.isEmpty()) {
            showError("Enter a municipality name to delete.");
            return;
        }

        try {
            boolean deleted = manager.delete(name);

            if (!deleted) {
                JOptionPane.showMessageDialog(this, "Record not found.");
                return;
            }

            refreshTable();
            deleteField.setText("");
            JOptionPane.showMessageDialog(this, "Record deleted.");
        } catch (IOException exception) {
            showError(exception.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public interface DatabaseManagerBridge {
        MunicipalityRecord find(String name) throws IOException;

        boolean insert(String name, int population, int altitude) throws IOException;

        boolean delete(String name) throws IOException;

        List<MunicipalityRecord> getAllRecords() throws IOException;

        void printAll() throws IOException;

        void printInfo() throws IOException;

        void close() throws IOException;
    }
}