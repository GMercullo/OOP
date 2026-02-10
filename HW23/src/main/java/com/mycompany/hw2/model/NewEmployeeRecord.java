package com.mycompany.hw2.model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.*;
import java.util.List;

/**
 * Handles the user interface for adding and editing employee records.
 * Collects and validates input, then creates or updates an EmployeeData object.
 */
public class NewEmployeeRecord extends JDialog {
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 600;
    private static final String EMPLOYEE_CSV_PATH = "src/MotorPH Employee Data - Employee Details.csv";
    private String filePath = EMPLOYEE_CSV_PATH;
    private JTextField firstNameField, lastNameField, phoneField, addressField, positionField, statusField, supervisorField;
    private JTextField sssField, philHealthField, tinField, pagIbigField;
    private JTextField salaryField, riceField, phoneAllowanceField, clothingField, hourlyRateField;
    private JComboBox<String> birthMonthDropdown;
    private JComboBox<Integer> birthDayDropdown;
    private JComboBox<Integer> birthYearDropdown;

    private EmployeeData employeeToUpdate;
    private boolean updateMode = false;
    private EmployeeData newEmployee;

    public NewEmployeeRecord(JFrame parent) {
        super(parent, "Add New Employee", true);
        initUI(parent);
    }

    public NewEmployeeRecord(JFrame parent, EmployeeData existingEmp) {
        super(parent, "Edit Employee", true);
        this.employeeToUpdate = existingEmp;
        this.updateMode = true;
        initUI(parent);
        populateForm(existingEmp);
    }

    private void initUI(JFrame parent) {
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton(updateMode ? "Update" : "Save");
        saveButton.addActionListener(updateMode ? this::onUpdate : this::onSave);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        firstNameField = new JTextField(); lastNameField = new JTextField();
        phoneField = new JTextField(); addressField = new JTextField();
        positionField = new JTextField(); statusField = new JTextField(); supervisorField = new JTextField();

        sssField = new JTextField(); philHealthField = new JTextField(); tinField = new JTextField(); pagIbigField = new JTextField();
        salaryField = new JTextField(); riceField = new JTextField(); phoneAllowanceField = new JTextField(); clothingField = new JTextField();
        hourlyRateField = new JTextField(); hourlyRateField.setEditable(false);

        // Month names from DateFormatSymbols
        String[] months = new java.text.DateFormatSymbols().getMonths();
        birthMonthDropdown = new JComboBox<>(Arrays.copyOf(months, 12)); // Only Jan–Dec

        // Days: 1 to 31
        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) {
            days[i] = i + 1;
        }
        birthDayDropdown = new JComboBox<>(days);

        // Years: 1925 to current year
        birthYearDropdown = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = 1925; y <= currentYear; y++) {
            birthYearDropdown.addItem(y);
        }

        addField(form, "First Name:", firstNameField, true);
        addField(form, "Last Name:", lastNameField, true);
        addField(form, "Birth Month:", birthMonthDropdown, true);
        addField(form, "Birth Day:", birthDayDropdown, true);
        addField(form, "Birth Year:", birthYearDropdown, true);
        addField(form, "Phone:", phoneField, true);
        addField(form, "Address:", addressField, true);
        addField(form, "Position:", positionField, true);
        addField(form, "Status:", statusField, true);
        addField(form, "Supervisor:", supervisorField, true);
        addField(form, "SSS #:", sssField, true);
        addField(form, "PhilHealth #:", philHealthField, true);
        addField(form, "TIN #:", tinField, true);
        addField(form, "Pag-IBIG #:", pagIbigField, true);
        addField(form, "Basic Salary:", salaryField, true);
        addField(form, "Rice Subsidy:", riceField, false);
        addField(form, "Phone Allowance:", phoneAllowanceField, false);
        addField(form, "Clothing Allowance:", clothingField, false);
        addField(form, "Hourly Rate:", hourlyRateField, false); // auto-calculated

        form.add(new JLabel("")); // empty cell for alignment
        form.add(new JLabel("<html><font color='red'>*</font> Required fields</html>"));

        salaryField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void updateHourlyRate() {
                try {
                    double salary = Double.parseDouble(salaryField.getText());
                    double hourly = salary / (22 * 8);
                    hourlyRateField.setText(String.format("%.2f", hourly));
                } catch (NumberFormatException e) {
                    hourlyRateField.setText("");
                }
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateHourlyRate(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateHourlyRate(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateHourlyRate(); }
        });

        return form;
    }

    private void addField(JPanel panel, String labelText, JComponent field, boolean isRequired) {
        JLabel label = new JLabel();
        if (isRequired) {
            label.setText("<html>" + labelText + " <font color='red'>*</font></html>");
        } else {
            label.setText(labelText);
        }
        panel.add(label);
        panel.add(field);
    }

    private void populateForm(EmployeeData emp) {
        firstNameField.setText(emp.getFirstName());
        lastNameField.setText(emp.getLastName());
        Calendar cal = Calendar.getInstance();
        cal.setTime(emp.getBirthDate());
        birthMonthDropdown.setSelectedIndex(cal.get(Calendar.MONTH));
        birthDayDropdown.setSelectedItem(cal.get(Calendar.DAY_OF_MONTH));
        birthYearDropdown.setSelectedItem(cal.get(Calendar.YEAR));

        phoneField.setText(emp.getPhoneNumber());
        addressField.setText(emp.getAddress());
        positionField.setText(emp.getPosition());
        statusField.setText(emp.getStatus());
        supervisorField.setText(emp.getSupervisor());

        GovernmentDetails gov = emp.getGovernmentDetails();
        sssField.setText(gov.getSssNumber());
        philHealthField.setText(gov.getPhilHealthNumber());
        tinField.setText(gov.getTinNumber());
        pagIbigField.setText(gov.getPagIbigNumber());

        CompensationDetails comp = emp.getCompensation();
        salaryField.setText(String.valueOf(comp.getBasicSalary()));
        riceField.setText(String.valueOf(comp.getRiceSubsidy()));
        phoneAllowanceField.setText(String.valueOf(comp.getPhoneAllowance()));
        clothingField.setText(String.valueOf(comp.getClothingAllowance()));
        hourlyRateField.setText(String.format("%.2f", comp.getHourlyRate()));
    }

    private void onSave(ActionEvent e) {
        if (!validateRequiredFields()) return;

        try {
            List<EmployeeData> allEmployees = CSVHandler.loadAllEmployees(filePath);
            int newId = allEmployees.stream().mapToInt(EmployeeData::getEmployeeId).max().orElse(0) + 1;

            newEmployee = createEmployeeFromForm(newId);  // Save to field
            CSVHandler.appendEmployeeToCSV(filePath, newEmployee);

            JOptionPane.showMessageDialog(this, "Employee added successfully!");
            dispose();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onUpdate(ActionEvent e) {
        if (!validateRequiredFields()) return;

        try {
            List<EmployeeData> allEmployees = CSVHandler.loadAllEmployees(filePath);
            newEmployee = createEmployeeFromForm(employeeToUpdate.getEmployeeId());  // Save to field

            boolean updated = false;
            for (int i = 0; i < allEmployees.size(); i++) {
                if (allEmployees.get(i).getEmployeeId() == newEmployee.getEmployeeId()) {
                    allEmployees.set(i, newEmployee);
                    updated = true;
                    break;
                }
            }

            if (updated) {
                CSVHandler.saveEmployeeToCSV(filePath, allEmployees);
                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Employee ID not found.");
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public EmployeeData getNewEmployee() {
        return newEmployee;
    }

    private EmployeeData createEmployeeFromForm(int empId) throws ParseException {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String position = positionField.getText().trim();
        String status = statusField.getText().trim().toLowerCase();
        String supervisor = supervisorField.getText().trim();

        int day = (int) birthDayDropdown.getSelectedItem();
        int month = birthMonthDropdown.getSelectedIndex(); // 0-based
        int year = (int) birthYearDropdown.getSelectedItem();

        Calendar cal = Calendar.getInstance();
        cal.setLenient(false); // Make Calendar strict
        try {
            cal.set(year, month, day);
            cal.getTime(); // Forces validation
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid birth date selected. Please choose a valid date.",
                    "Invalid Date",
                    JOptionPane.ERROR_MESSAGE);
            throw new ParseException("Invalid birth date", 0); // rethrow to abort saving
        }
        Date birthDate = cal.getTime();

        GovernmentDetails gov = new GovernmentDetails(
                sssField.getText().trim(),
                philHealthField.getText().trim(),
                tinField.getText().trim(),
                pagIbigField.getText().trim()
        );

        double basic = Double.parseDouble(salaryField.getText().trim());
        double rice = Double.parseDouble(riceField.getText().trim());
        double phoneAllowance = Double.parseDouble(phoneAllowanceField.getText().trim());
        double clothing = Double.parseDouble(clothingField.getText().trim());
        double hourlyRate = Double.parseDouble(hourlyRateField.getText().trim());
        double grossSemiMonthly = basic / 2.0;

        CompensationDetails comp = new CompensationDetails(basic, rice, phoneAllowance, clothing, grossSemiMonthly, hourlyRate);

        if (status.contains("probationary")) {
            return new ProbationaryEmployee(empId, firstName, lastName, birthDate, address, phone, status, position, supervisor, comp, gov);
        } else if (status.contains("contractual")) {
            return new ContractualEmployee(empId, firstName, lastName, birthDate, address, phone, status, position, supervisor, comp, gov);
        } else {
            return new RegularEmployee(empId, firstName, lastName, birthDate, address, phone, status, position, supervisor, comp, gov);
        }
    }
    private boolean validateRequiredFields() {
        Map<String, JTextField> requiredFields = new LinkedHashMap<>();
        requiredFields.put("First Name", firstNameField);
        requiredFields.put("Last Name", lastNameField);
        requiredFields.put("Phone", phoneField);
        requiredFields.put("Address", addressField);
        requiredFields.put("Position", positionField);
        requiredFields.put("Status", statusField);
        requiredFields.put("Supervisor", supervisorField);
        requiredFields.put("SSS #", sssField);
        requiredFields.put("PhilHealth #", philHealthField);
        requiredFields.put("TIN #", tinField);
        requiredFields.put("Pag-IBIG #", pagIbigField);
        requiredFields.put("Basic Salary", salaryField);

        for (Map.Entry<String, JTextField> entry : requiredFields.entrySet()) {
            if (entry.getValue().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        entry.getKey() + " cannot be empty",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                entry.getValue().requestFocus();
                return false;
            }
        }

        // Validate name fields (letters only)
        if (!firstNameField.getText().trim().matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(this, "First name must contain letters only.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            firstNameField.requestFocus();
            return false;
        }
        if (!lastNameField.getText().trim().matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(this, "Last name must contain letters only.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            lastNameField.requestFocus();
            return false;
        }

        // Validate numeric fields (digits only)
        JTextField[] numericFields = {phoneField, sssField, philHealthField, tinField, pagIbigField};
        String[] numericLabels = {"Phone", "SSS #", "PhilHealth #", "TIN #", "Pag-IBIG #"};
        for (int i = 0; i < numericFields.length; i++) {
            if (!numericFields[i].getText().trim().matches("[0-9\\- ]+")) {
                JOptionPane.showMessageDialog(this, numericLabels[i] + " must contain digits, dashes, or spaces only.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                numericFields[i].requestFocus();
                return false;
            }
        }

        return true;
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}


