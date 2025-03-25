package edu.bbte.idde.mzim2273.presentation.buttons;

import edu.bbte.idde.mzim2273.business.service.HikeService;
import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.model.Hike;
import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

public class UpdateButton {
    private final HikeService hikeService;
    private final DefaultTableModel tableModel;

    public UpdateButton(HikeService hikeService, DefaultTableModel tableModel) {
        this.hikeService = hikeService;
        this.tableModel = tableModel;
    }

    public JPanel createUpdateHikePanel() {
        final JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));

        final JTextField idField = createTextField();
        final JTextField nameField = createTextField();
        final JTextField locationField = createTextField();

        final JDatePickerImpl datePicker = createDatePicker();
        final JSpinner timeSpinner = createTimeSpinner();
        final JSpinner priceField = createPriceField();

        final JButton updateButton =
                createUpdateButton(panel, idField, nameField, locationField, datePicker, timeSpinner, priceField);

        panel.add(new JLabel("Hike ID to Update:"));
        panel.add(idField);
        panel.add(new JLabel("New Name:"));
        panel.add(nameField);
        panel.add(new JLabel("New Location:"));
        panel.add(locationField);
        panel.add(new JLabel("New Date:"));
        panel.add(datePicker);
        panel.add(new JLabel("New Time:"));
        panel.add(timeSpinner);
        panel.add(new JLabel("New Price:"));
        panel.add(priceField);
        panel.add(new JLabel());
        panel.add(updateButton);

        return panel;
    }

    private JTextField createTextField() {
        return new JTextField(20);
    }

    private JDatePickerImpl createDatePicker() {
        final UtilDateModel dateModel = new UtilDateModel();
        final Properties dateProperties = new Properties();
        dateProperties.put("text.today", "Today");
        dateProperties.put("text.month", "Month");
        dateProperties.put("text.year", "Year");
        final JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, dateProperties);
        return new JDatePickerImpl(datePanel, new DateComponentFormatter());
    }

    private JSpinner createTimeSpinner() {
        final SpinnerDateModel timeModel = new SpinnerDateModel();
        final JSpinner timeSpinner = new JSpinner(timeModel);
        final JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        return timeSpinner;
    }

    private JSpinner createPriceField() {
        final SpinnerNumberModel numberModel = new SpinnerNumberModel(10.0, 0.0, 100.0, 1.0);
        return new JSpinner(numberModel);
    }

    private JButton createUpdateButton(final JPanel panel, final JTextField idField, final JTextField nameField,
                                       final JTextField locationField, final JDatePickerImpl datePicker,
                                       final JSpinner timeSpinner, final JSpinner priceField) {
        final JButton updateButton = new JButton("Update Hike");
        updateButton.addActionListener(e ->
                handleUpdateButtonClick(panel, idField, nameField, locationField, datePicker, timeSpinner, priceField));
        return updateButton;
    }

    private void handleUpdateButtonClick(final JPanel panel, final JTextField idField, final JTextField nameField,
                                         final JTextField locationField, final JDatePickerImpl datePicker,
                                         final JSpinner timeSpinner, final JSpinner priceField) {
        try {
            final String idText = idField.getText();

            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter a valid Hike ID.");
                return;
            }

            final Long id = Long.parseLong(idText);

            Hike hike = hikeService.getHikeById(id, false);
            if (hike == null) {
                JOptionPane.showMessageDialog(panel, "No hike found with ID: " + id);
                return;
            }

            final String name = nameField.getText().isEmpty() ? hike.getName() : nameField.getText();
            final String location = locationField.getText().isEmpty()
                    ? hike.getStartLocation() : locationField.getText();

            final LocalDate date = (datePicker.getModel().getValue() == null)
                    ? hike.getStartDate()
                    : ((Date) datePicker.getModel().getValue())
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            final Date timeValue = (Date) timeSpinner.getValue();
            final LocalTime time = timeValue == null
                    ? hike.getStartTime()
                    : LocalTime.of(timeValue.getHours(), timeValue.getMinutes());

            final double price = (double) priceField.getValue();

            hike.setName(name);
            hike.setStartLocation(location);
            hike.setStartDate(date);
            hike.setStartTime(time);
            hike.setPrice(price);

            hikeService.updateHike(hike, false);
            JOptionPane.showMessageDialog(panel, "Hike updated successfully!");

            updateTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel, "Invalid ID format. Please enter a valid numeric ID.");
        } catch (EntityNotFoundException | RepositoryException ex) {
            JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
        }
    }

    private void updateTable() throws RepositoryException {
        tableModel.setRowCount(0);
        final Collection<Hike> hikes = hikeService.getAllHikes(false);
        for (final Hike hike : hikes) {
            final Object[] row = {
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
