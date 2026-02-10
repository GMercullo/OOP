package com.mycompany.hw2.model;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A Swing panel that displays a detailed payroll report for an employee,
 * including personal info, salary breakdown, hours worked, and deductions.
 *
 * Used in conjunction with PayrollService() in HW2.
 *
 * Author: gmmercullo
 */
public class PayrollReport extends JPanel {
    // Labels used in the payroll report display
    private static final String[] PAYROLL_LABELS = {
            "Employee #:", "Last Name:", "First Name:", "Birth Date:", "Address:",
            "Phone #:", "Position:", "Status:", "Supervisor:",
            "SSS #:", "PhilHealth #:", "TIN #:", "Pag-IBIG #:",
            "Basic Salary:", "Rice Subsidy:", "Phone Subsidy:", "Clothing Allowance:",
            "Gross Semi-Monthly Salary:", "Hourly Rate:", "Month:",
            "Regular Hours:", "Overtime Hours:", "Gross Salary:",
            "SSS Deduction:", "PhilHealth Deduction:", "Pag-IBIG Deduction:", "Withholding Tax:",
            "Net Salary:"
    };

    // Map to store JLabels for each field so values can be updated dynamically
    private final Map<String, JLabel> labelMap = new HashMap<>();

    // Constructor: sets up the UI components in a grid layout
    public PayrollReport() {
        setLayout(new GridBagLayout()); // Layout manager for flexible grid design
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around the panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Padding between components
        gbc.anchor = GridBagConstraints.WEST; // Align labels to the left
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        gbc.weightx = 1.0; // Allow labels to grow horizontally

        int row = 0;
        for (String label : PAYROLL_LABELS) {
            gbc.gridx = 0;
            gbc.gridy = row;

            // Create label for the field name (left column)
            JLabel nameLabel = new JLabel(label);
            nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            add(nameLabel, gbc); // Add name label to panel

            gbc.gridx = 1;

            // Create label for the field value (right column, initially empty)
            JLabel valueLabel = new JLabel(" ");
            valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            labelMap.put(label, valueLabel); // Store reference in the map for future updates
            add(valueLabel, gbc); // Add value label to panel

            row++; // Move to next row
        }
    }


    public void setValue(String label, String value) {
        JLabel field = labelMap.get(label);
        if (field != null) {
            field.setText(value); // Update the label with the new value
        }
    }

    public void clearReport() {
        for (JLabel label : labelMap.values()) {
            label.setText(" ");
        }
    }
}