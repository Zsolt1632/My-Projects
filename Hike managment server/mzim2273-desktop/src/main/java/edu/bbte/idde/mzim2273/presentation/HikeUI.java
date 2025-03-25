package edu.bbte.idde.mzim2273.presentation;

import edu.bbte.idde.mzim2273.business.service.HikeService;
import edu.bbte.idde.mzim2273.business.service.exception.MissingArgumentException;
import edu.bbte.idde.mzim2273.data.access.InMemoryHikeDAO;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.model.Hike;
import edu.bbte.idde.mzim2273.presentation.buttons.AddButton;
import edu.bbte.idde.mzim2273.presentation.buttons.DeleteButton;
import edu.bbte.idde.mzim2273.presentation.buttons.FilterButton;
import edu.bbte.idde.mzim2273.presentation.buttons.UpdateButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

public final class HikeUI {
    private final HikeService hikeService;
    private DefaultTableModel tableModel;

    public static HikeUI create(HikeService hikeService) {
        try {
            return new HikeUI(hikeService);
        } catch (RepositoryException e) {
            // Handle the exception (log it, show message, etc.)
            JOptionPane.showMessageDialog(null, "Error creating Hike UI: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null; // or handle in another appropriate way
        }
    }

    private HikeUI(HikeService hikeService) throws RepositoryException {
        this.hikeService = hikeService;
        if (hikeService.getHikeDAO() instanceof InMemoryHikeDAO) {
            loadSampleData();
        }
    }


    private void loadSampleData() throws RepositoryException {
        for (int i = 1; i <= 20; i++) {
            String name = "Hike " + i;
            String location = "Location " + i;

            LocalDate date = LocalDate.now().plusDays(i % 30);
            LocalTime time = LocalTime.of(6 + i % 3 * 2, 0);
            double price = 10.0 + (i % 41);
            Hike hike = new Hike(name, location, date, time, price);
            try {
                hikeService.addHike(hike);
            } catch (MissingArgumentException | RepositoryException e) {
                throw new RepositoryException(e);
            }
        }
    }

    public void display() throws RepositoryException {
        JFrame frame = new JFrame("Hike Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Name", "Location", "Date", "Time", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable hikeTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(hikeTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        JTabbedPane tabbedPane = new JTabbedPane();
        AddButton addButton = new AddButton(hikeService, tableModel);
        DeleteButton deleteButton = new DeleteButton(hikeService, tableModel);
        FilterButton filterButton = new FilterButton(hikeService, tableModel);
        UpdateButton updateButton = new UpdateButton(hikeService, tableModel);

        tabbedPane.addTab("Add Hike", addButton.createAddHikePanel());
        tabbedPane.addTab("Delete Hike", deleteButton.createDeleteHikePanel());
        tabbedPane.addTab("Find Hike by ID", filterButton.createFilterHikePanel());
        tabbedPane.addTab("Update Hike", updateButton.createUpdateHikePanel());

        frame.getContentPane().add(tabbedPane, BorderLayout.NORTH);
        frame.setVisible(true);

        updateHikeList();
    }

    private void updateHikeList() throws RepositoryException {
        tableModel.setRowCount(0);

        Collection<Hike> hikes = hikeService.getAllHikes();
        for (Hike hike : hikes) {
            Object[] row = {
                    hike.getId(),
                    hike.getName(),
                    hike.getStartLocation(),
                    hike.getStartDate(),
                    hike.getStartTime(),
                    hike.getPrice()
            };
            tableModel.addRow(row);
        }
    }
}
