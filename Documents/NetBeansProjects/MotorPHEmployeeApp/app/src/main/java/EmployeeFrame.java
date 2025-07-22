import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

public class EmployeeFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private EmployeeManager manager;
    private String[] columns = {"Employee Number", "Last Name", "First Name", "SSS Number", "PhilHealth Number", "TIN", "Pag-IBIG Number"};
    private JButton addBtn, updateBtn, deleteBtn, viewBtn, computeSalaryBtn;
    private JTextField[] fields;
    private int selectedRow = -1;

    public EmployeeFrame() {
        setTitle("MotorPH Employee Records");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        manager = new EmployeeManager();
        manager.getAll().addAll(EmployeeCSV.readEmployees("employees.csv"));

        model = new DefaultTableModel(columns, 0);
        for (Employee e : manager.getAll()) model.addRow(e.toArray());

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel formPanel = new JPanel(new GridLayout(7,2,5,5));
        fields = new JTextField[7];
        String[] fieldNames = columns;
        for (int i = 0; i < 7; i++) {
            formPanel.add(new JLabel(fieldNames[i]));
            fields[i] = new JTextField();
            formPanel.add(fields[i]);
        }

        addBtn = new JButton("New Employee");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        viewBtn = new JButton("View Employee");
        computeSalaryBtn = new JButton("Compute Salary");

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(viewBtn);
        btnPanel.add(computeSalaryBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);
        add(btnPanel, BorderLayout.SOUTH);

        setVisible(true);

        table.getSelectionModel().addListSelectionListener(e -> {
            selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                for (int i = 0; i < 7; i++) fields[i].setText(model.getValueAt(selectedRow, i).toString());
            }
        });

        addBtn.addActionListener(e -> addEmployee());
        updateBtn.addActionListener(e -> updateEmployee());
        deleteBtn.addActionListener(e -> deleteEmployee());
        viewBtn.addActionListener(e -> viewEmployee());
        computeSalaryBtn.addActionListener(e -> computeSalary());
    }

    private void addEmployee() {
        try {
            String empNumber = fields[0].getText().trim();
            if (empNumber.isEmpty() || !empNumber.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Invalid Employee Number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (Employee emp : manager.getAll())
                if (emp.getEmpNumber().equals(empNumber)) {
                    JOptionPane.showMessageDialog(this, "Employee Number already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            Employee emp = new Employee(
                fields[0].getText().trim(), fields[1].getText().trim(),
                fields[2].getText().trim(), fields[3].getText().trim(),
                fields[4].getText().trim(), fields[5].getText().trim(),
                fields[6].getText().trim()
            );
            manager.add(emp);
            EmployeeCSV.appendEmployee("employees.csv", emp);
            model.addRow(emp.toArray());
            JOptionPane.showMessageDialog(this, "Employee added!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding employee.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an employee to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String empNumber = fields[0].getText().trim();
            if (empNumber.isEmpty() || !empNumber.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Invalid Employee Number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Employee emp = new Employee(
                fields[0].getText().trim(), fields[1].getText().trim(),
                fields[2].getText().trim(), fields[3].getText().trim(),
                fields[4].getText().trim(), fields[5].getText().trim(),
                fields[6].getText().trim()
            );
            manager.update(selectedRow, emp);
            EmployeeCSV.writeEmployees("employees.csv", manager.getAll());
            for (int i = 0; i < 7; i++) model.setValueAt(emp.toArray()[i], selectedRow, i);
            JOptionPane.showMessageDialog(this, "Employee updated!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating employee.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an employee to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        manager.delete(selectedRow);
        EmployeeCSV.writeEmployees("employees.csv", manager.getAll());
        model.removeRow(selectedRow);
        JOptionPane.showMessageDialog(this, "Employee deleted!");
    }

    private void viewEmployee() {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an employee to view.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Employee emp = manager.get(selectedRow);
        JOptionPane.showMessageDialog(this,
            "Employee Details:\n" +
            "Number: " + emp.getEmpNumber() + "\n" +
            "Name: " + emp.getFirstName() + " " + emp.getLastName() + "\n" +
            "SSS: " + emp.getSssNumber() + "\n" +
            "PhilHealth: " + emp.getPhilHealthNumber() + "\n" +
            "TIN: " + emp.getTin() + "\n" +
            "Pag-IBIG: " + emp.getPagIbigNumber(),
            "Employee Details",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void computeSalary() {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an employee.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String month = JOptionPane.showInputDialog(this, "Enter month for salary computation (e.g. May):");
        String daysStr = JOptionPane.showInputDialog(this, "Enter days worked for " + month + ":");
        int daysWorked = 0;
        try {
            daysWorked = Integer.parseInt(daysStr.trim());
            if (daysWorked < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid days worked!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double salary = SalaryCalculator.calculate(manager.get(selectedRow), month, daysWorked);
        JOptionPane.showMessageDialog(this,
            "Salary Details for " + month + "\n\n" +
            "Employee: " + manager.get(selectedRow).getFirstName() + " " + manager.get(selectedRow).getLastName() + "\n" +
            "Salary: " + salary,
            "Salary Computation",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}