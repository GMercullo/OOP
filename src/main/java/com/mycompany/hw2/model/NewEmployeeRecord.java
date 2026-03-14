package com.mycompany.hw2.model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.*;
import java.util.List;

/**
 * Handles the user interface for adding and editing employee records.
 * NOW WITH COMPLETE VALIDATION: Phone(9dig), SSS(10dig), PhilHealth(12), TIN(12), PagIbig(12)
 */
public class NewEmployeeRecord extends JDialog {
    private static final int WINDOW_WIDTH = 450;
    private static final int WINDOW_HEIGHT = 650;
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

        // Month names
        String[] months = new java.text.DateFormatSymbols().getMonths();
        birthMonthDropdown = new JComboBox<>(Arrays.copyOf(months, 12));

        // Days: 1 to 31
        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) {
            days[i] = i + 1;
        }
        birthDayDropdown = new JComboBox<>(days);

        // Years: 1925 to current
        birthYearDropdown = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = 1925; y <= currentYear; y++) {
            birthYearDropdown.addItem(y);
        }

        // ADD FIELDS WITH PROPER LABELS & VALIDATION HINTS
        addField(form, "First Name *", firstNameField);
        addField(form, "Last Name *", lastNameField);
        addField(form, "Birth Month *", birthMonthDropdown);
        addField(form, "Birth Day *", birthDayDropdown);
        addField(form, "Birth Year *", birthYearDropdown);
        addField(form, "Phone (983-606-799) *", phoneField);
        addField(form, "Address *", addressField);
        addField(form, "Position *", positionField);
        addField(form, "Status *", statusField);
        addField(form, "Supervisor *", supervisorField);
        addField(form, "SSS (55-4476527-2) *", sssField);
        addField(form, "PhilHealth (12 digits) *", philHealthField);
        addField(form, "TIN (888-572-294-000) *", tinField);
        addField(form, "Pag-Ibig (12 digits) *", pagIbigField);
        addField(form, "Basic Salary *", salaryField);
        addField(form, "Rice Subsidy", riceField);
        addField(form, "Phone Allowance", phoneAllowanceField);
        addField(form, "Clothing Allowance", clothingField);
        addField(form, "Hourly Rate (auto)", hourlyRateField);

        form.add(new JLabel(""));
        form.add(new JLabel("<html><font color='red'>* Required - See format hints</font></html>"));

        // AUTO-CALCULATE HOURLY RATE
        salaryField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void updateHourlyRate() {
                try {
                    double salary = Double.parseDouble(salaryField.getText());
                    double hourly = salary / (22 * 8); // 22 days * 8 hours
                    hourlyRateField.setText(String.format("₱%.2f", hourly));
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

    private void addField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setToolTipText(getFieldHint(labelText));
        panel.add(label);
        panel.add(field);
    }

    private String getFieldHint(String label) {
        if (label.contains("Phone")) return "Format: 983-606-799 (exactly 9 digits)";
        if (label.contains("SSS")) return "Format: 55-4476527-2 (exactly 10 digits)";
        if (label.contains("PhilHealth")) return "Exactly 12 digits (no dashes)";
        if (label.contains("TIN")) return "Format: 888-572-294-000 (exactly 12 digits)";
        if (label.contains("Pag-Ibig")) return "Exactly 12 digits (no dashes)";
        if (label.contains("Salary")) return "Enter monthly basic salary";
        return "";
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

    /** ✅ FIXED VALIDATION - ALL REQUIREMENTS IMPLEMENTED */
    private boolean validateRequiredFields() {
        // Reset all borders first
        resetAllBorders();

        List<String> errors = new ArrayList<>();

        // 1. REQUIRED FIELDS CHECK
        if (firstNameField.getText().trim().isEmpty()) errors.add("First Name required");
        if (lastNameField.getText().trim().isEmpty()) errors.add("Last Name required");
        if (phoneField.getText().trim().isEmpty()) errors.add("Phone required");
        if (addressField.getText().trim().isEmpty()) errors.add("Address required");
        if (positionField.getText().trim().isEmpty()) errors.add("Position required");
        if (statusField.getText().trim().isEmpty()) errors.add("Status required");
        if (supervisorField.getText().trim().isEmpty()) errors.add("Supervisor required");
        if (sssField.getText().trim().isEmpty()) errors.add("SSS required");
        if (philHealthField.getText().trim().isEmpty()) errors.add("PhilHealth required");
        if (tinField.getText().trim().isEmpty()) errors.add("TIN required");
        if (pagIbigField.getText().trim().isEmpty()) errors.add("Pag-Ibig required");
        if (salaryField.getText().trim().isEmpty()) errors.add("Salary required");

        // 2. PHONE VALIDATION: 983-606-799 (exactly 9 digits)
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !phone.matches("\\d{3}-\\d{3}-\\d{3}")) {
            errors.add("Phone: Use XXX-XXX-XXX format (9 digits)");
            phoneField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }

        // 3. SSS VALIDATION: 55-4476527-2 (exactly 10 digits)
        String sss = sssField.getText().trim();
        if (!sss.isEmpty() && !sss.matches("\\d{2}-\\d{7}-\\d")) {
            errors.add("SSS: Use XX-XXXXXXX-X format (10 digits)");
            sssField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }

        // 4. PHILHEALTH: Exactly 12 digits
        String phil = philHealthField.getText().trim();
        if (!phil.isEmpty() && !phil.matches("\\d{12}")) {
            errors.add("PhilHealth: Exactly 12 digits only");
            philHealthField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }

        // 5. TIN: 888-572-294-000 (exactly 12 digits)
        String tin = tinField.getText().trim();
        if (!tin.isEmpty() && !tin.matches("\\d{3}-\\d{3}-\\d{3}-\\d{3}")) {
            errors.add("TIN: Use XXX-XXX-XXX-XXX format (12 digits)");
            tinField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }

        // 6. PAGIBIG: Exactly 12 digits
        String pagibig = pagIbigField.getText().trim();
        if (!pagibig.isEmpty() && !pagibig.matches("\\d{12}")) {
            errors.add("Pag-Ibig: Exactly 12 digits only");
            pagIbigField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }

        // 7. SALARY VALIDATION
        String salaryText = salaryField.getText().trim();
        if (!salaryText.isEmpty()) {
            try {
                double salary = Double.parseDouble(salaryText);
                if (salary <= 0) {
                    errors.add("Salary must be greater than 0");
                    salaryField.setBorder(BorderFactory.createLineBorder(Color.RED));
                }
            } catch (NumberFormatException e) {
                errors.add("Salary must be a valid number");
                salaryField.setBorder(BorderFactory.createLineBorder(Color.RED));
            }
        }

        // 8. NAME VALIDATION (letters only)
        if (!firstNameField.getText().trim().matches("[a-zA-Z\\s]+")) {
            errors.add("First Name: Letters only");
            firstNameField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
        if (!lastNameField.getText().trim().matches("[a-zA-Z\\s]+")) {
            errors.add("Last Name: Letters only");
            lastNameField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }

        if (!errors.isEmpty()) {
            String errorMsg = String.join("\n• ", errors);
            JOptionPane.showMessageDialog(this,
                    "<html><b>VALIDATION FAILED:</b><br>• " + errorMsg + "</html>",
                    "❌ Fix These Errors", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void resetAllBorders() {
        // Reset all field borders to default
        JTextField[] fields = {firstNameField, lastNameField, phoneField, addressField,
                positionField, statusField, supervisorField, sssField,
                philHealthField, tinField, pagIbigField, salaryField};
        for (JTextField field : fields) {
            field.setBorder(UIManager.getBorder("TextField.border"));
        }
    }

    private void onSave(ActionEvent e) {
        if (!validateRequiredFields()) return;

        try {
            List<EmployeeData> allEmployees = CSVHandler.loadAllEmployees(filePath);
            int newId = allEmployees.stream().mapToInt(EmployeeData::getEmployeeId).max().orElse(0) + 1;

            newEmployee = createEmployeeFromForm(newId);
            CSVHandler.appendEmployeeToCSV(filePath, newEmployee);

            JOptionPane.showMessageDialog(this, "✅ Employee added successfully!\nID: " + newId);
            dispose();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onUpdate(ActionEvent e) {
        if (!validateRequiredFields()) return;

        try {
            List<EmployeeData> allEmployees = CSVHandler.loadAllEmployees(filePath);
            newEmployee = createEmployeeFromForm(employeeToUpdate.getEmployeeId());

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
                JOptionPane.showMessageDialog(this, "✅ Employee updated successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Employee ID not found.");
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public EmployeeData getNewEmployee() {
        return newEmployee;
    }

    // Rest of your existing methods remain unchanged...
    private EmployeeData createEmployeeFromForm(int empId) throws ParseException {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String position = positionField.getText().trim();
        String status = statusField.getText().trim().toLowerCase();
        String supervisor = supervisorField.getText().trim();

        int day = (Integer) birthDayDropdown.getSelectedItem();
        int month = birthMonthDropdown.getSelectedIndex();
        int year = (Integer) birthYearDropdown.getSelectedItem();

        Calendar cal = Calendar.getInstance();
        cal.setLenient(false);
        cal.set(year, month, day);
        cal.getTime(); // Validate date
        Date birthDate = cal.getTime();

        GovernmentDetails gov = new GovernmentDetails(
                sssField.getText().trim(),
                philHealthField.getText().trim(),
                tinField.getText().trim(),
                pagIbigField.getText().trim()
        );

        double basic = Double.parseDouble(salaryField.getText().trim());
        double rice = parseDoubleOrZero(riceField.getText().trim());
        double phoneAllowance = parseDoubleOrZero(phoneAllowanceField.getText().trim());
        double clothing = parseDoubleOrZero(clothingField.getText().trim());
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

    private double parseDoubleOrZero(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}
