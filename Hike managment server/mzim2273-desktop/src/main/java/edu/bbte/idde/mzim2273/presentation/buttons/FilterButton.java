package edu.bbte.idde.mzim2273.presentation.buttons;

import edu.bbte.idde.mzim2273.business.service.HikeService;
import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.model.Hike;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collection;

public class FilterButton {
    private final HikeService hikeService;
    private final DefaultTableModel tableModel;

    public FilterButton(HikeService hikeService, DefaultTableModel tableModel) {
        this.hikeService = hikeService;
        this.tableModel = tableModel;
    }

    public JPanel createFilterHikePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));

        JTextField idField = new JTextField(20);
        JButton findButton = createFindButton(idField, panel);
        JButton resetButton = createResetButton(idField);

        panel.add(new JLabel("Hike ID to Search:"));  // Label for ID input
        panel.add(idField);                            // TextField for ID input
        panel.add(resetButton);                        // Reset button
        panel.add(findButton);                        // Filter button

        return panel;
    }

    private JButton createFindButton(JTextField idField, JPanel panel) {
        JButton findButton = new JButton("Search by ID");
        findButton.addActionListener(e -> {
            String idText = idField.getText();
            tableModel.setRowCount(0); // Clear current table rows
            try {
                if (idText.isEmpty()) {
                    populateTableWithAllHikes();
                } else {
                    searchAndDisplayHikeById(idText, panel);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid ID format. Please enter a valid numeric ID.");
            } catch (EntityNotFoundException | RepositoryException ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });
        return findButton;
    }

    private JButton createResetButton(JTextField idField) {
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            idField.setText(""); // Clear the ID field
            tableModel.setRowCount(0); // Clear current table rows
            populateTableWithAllHikes();
        });
        return resetButton;
    }

    private void populateTableWithAllHikes() {
        try {
            Collection<Hike> hikes = hikeService.getAllHikes();
            for (Hike hike : hikes) {
                addHikeToTable(hike);
            }
        } catch (RepositoryException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    private void searchAndDisplayHikeById(String idText, JPanel panel)
            throws RepositoryException, EntityNotFoundException {
        Long id = Long.parseLong(idText);
        Hike hike = hikeService.getHikeById(id);
        if (hike != null) {
            addHikeToTable(hike);
        } else {
            JOptionPane.showMessageDialog(panel, "No hike found with ID: " + id);
        }
    }

    private void addHikeToTable(Hike hike) {
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
