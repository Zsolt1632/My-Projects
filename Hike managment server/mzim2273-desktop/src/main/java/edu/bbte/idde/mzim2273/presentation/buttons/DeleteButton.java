package edu.bbte.idde.mzim2273.presentation.buttons;

import edu.bbte.idde.mzim2273.business.service.HikeService;
import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DeleteButton {
    private final HikeService hikeService;
    private final DefaultTableModel tableModel;

    public DeleteButton(HikeService hikeService, DefaultTableModel tableModel) {
        this.hikeService = hikeService;
        this.tableModel = tableModel;
    }

    public JPanel createDeleteHikePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));

        JTextField deleteIdField = new JTextField(20);
        JButton deleteButton = new JButton("Delete Hike");

        deleteButton.addActionListener(e -> {
            try {
                String idText = deleteIdField.getText();
                Long id = Long.parseLong(idText);
                hikeService.deleteHike(id);

                // Remove from the JTable
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).equals(id)) {
                        tableModel.removeRow(i);
                        break; // Break after removing to avoid ConcurrentModificationException
                    }
                }

                deleteIdField.setText(""); // Clear input field after deletion
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid ID format. Please enter a valid numeric ID.");
            } catch (RepositoryException | EntityNotFoundException ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        panel.add(new JLabel("Hike ID to Delete:"));
        panel.add(deleteIdField);
        panel.add(new JLabel());
        panel.add(deleteButton);

        return panel;
    }
}
