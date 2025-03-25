package edu.bbte.idde.mzim2273.presentation.buttons;

import edu.bbte.idde.mzim2273.business.service.HikeService;
import edu.bbte.idde.mzim2273.business.service.exception.MissingArgumentException;

import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.model.Hike;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Properties;

import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class AddButton {
    private final HikeService hikeService;
    private final DefaultTableModel tableModel;

    public AddButton(HikeService hikeService, DefaultTableModel tableModel) {
        this.hikeService = hikeService;
        this.tableModel = tableModel;
    }

    public JPanel createAddHikePanel() {
        // Date Picker
        UtilDateModel dateModel = new UtilDateModel();
        Properties dateProperties = new Properties();
        dateProperties.put("text.today", "Today");
        dateProperties.put("text.month", "Month");
        dateProperties.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, dateProperties);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateComponentFormatter());

        // Time Spinner
        SpinnerDateModel timeModel = new SpinnerDateModel();
        JSpinner timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);

        // Price Spinner
        SpinnerNumberModel numberModel = new SpinnerNumberModel(10.0, 0.0, 100.0, 1.0);
        JSpinner priceField = new JSpinner(numberModel);

        JTextField nameField = new JTextField(20);
        JTextField locationField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        JButton addButton = new JButton("Add Hike");
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String location = locationField.getText();

                // Get date from the date picker
                Date selectedDate = (Date) datePicker.getModel().getValue();

                // Check if selectedDate is null
                if (selectedDate == null) {
                    JOptionPane.showMessageDialog(panel, "Please select a date.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Exit if no date is selected
                }

                LocalDate date = selectedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

                // Get time from the time spinner
                Date timeValue = (Date) timeSpinner.getValue();
                LocalTime time = LocalTime.of(timeValue.getHours(), timeValue.getMinutes());

                double price = (double) priceField.getValue();
                Hike hike = new Hike(name, location, date, time, price);
                hikeService.addHike(hike);

                // Add to the JTable
                Object[] row = {
                        hike.getId(),
                        hike.getName(),
                        hike.getStartLocation(),
                        hike.getStartDate(),
                        hike.getStartTime(),
                        hike.getPrice()
                };
                tableModel.addRow(row);

                // Clear input fields after adding
                nameField.setText("");
                locationField.setText("");
                datePicker.getModel().setValue(null);
                timeSpinner.setValue(new Date());
                priceField.setValue(10.0); // Reset to default price
            } catch (MissingArgumentException | IllegalArgumentException | RepositoryException ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Date:"));
        panel.add(datePicker);
        panel.add(new JLabel("Time:"));
        panel.add(timeSpinner);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel());
        panel.add(addButton);

        return panel;
    }
}
