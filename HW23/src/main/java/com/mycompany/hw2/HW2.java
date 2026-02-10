package com.mycompany.hw2;

// Author: gmmercullo

import com.mycompany.hw2.model.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

import static com.mycompany.hw2.model.Payroll.PayrollCalc;

public class HW2 extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Employee employeeManager;
    private final DecimalFormat money = new DecimalFormat("\u20b1#,##0.00");
    private String role;


    private String loggedInUsername;

    public HW2(String userRole, String username) {
        this.role = userRole;
        this.loggedInUsername = username;
        setTitle("MotorPH Payroll System - Logged in as " + role);
            if (!role.equalsIgnoreCase("EMPLOYEE")) {
                setSize(500, 400);
            } else {
                setSize(1200, 600);
            }
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        employeeManager = new Employee();
        employeeManager.loadEmployeesFromCSV("src/MotorPH Employee Data - Employee Details.csv");

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createHomePanel(), "home");
        mainPanel.add(createAllEmployeePanel(), "allEmployees");

        add(mainPanel);
        cardLayout.show(mainPanel, "home");
        setVisible(true);
    }

    private JPanel createHomePanel() {
        if (role.equalsIgnoreCase("EMPLOYEE")) {
            return createEmployeeDashboard();
        }

        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("MotorPH Employee App - " + role + " Portal", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JButton viewAllButton = new JButton("View All Employee Records");
        viewAllButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewAllButton.setMaximumSize(new Dimension(200, 40));
        viewAllButton.addActionListener(e -> {
            setSize(1200, 900);
            cardLayout.show(mainPanel, "allEmployees");
        });

        JButton logOutButton = new JButton("Log Out");
        logOutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logOutButton.setMaximumSize(new Dimension(200, 40));
        logOutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JButton searchByIdButton = new JButton("Search Employee");
        searchByIdButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchByIdButton.setMaximumSize(new Dimension(200, 40));
        searchByIdButton.addActionListener(e -> showSearchEmployeeDialog());

        JButton searchPayCoverageButton = new JButton("Pay Coverage");
        searchPayCoverageButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchPayCoverageButton.setMaximumSize(new Dimension(200, 40));
        searchPayCoverageButton.addActionListener(e -> showSearchPayCoverageDialog());

        center.add(Box.createRigidArea(new Dimension(0, 60)));
        center.add(searchByIdButton);
        center.add(Box.createRigidArea(new Dimension(0, 10)));
        center.add(searchPayCoverageButton);

        JButton fileLeaveButton = new JButton("File Leave");
        fileLeaveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        fileLeaveButton.setMaximumSize(new Dimension(200, 40));
        fileLeaveButton.addActionListener(e -> showFileLeaveDialog());

        center.add(Box.createRigidArea(new Dimension(0, 10)));
        center.add(fileLeaveButton);

        if (role.equalsIgnoreCase("HR") || role.equalsIgnoreCase("ADMIN")) {
            JButton manageLeaveButton = new JButton("Manage Leaves");
            manageLeaveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            manageLeaveButton.setMaximumSize(new Dimension(200, 40));
            manageLeaveButton.addActionListener(e -> showLeaveManagementDialog());

            center.add(Box.createRigidArea(new Dimension(0, 10)));
            center.add(manageLeaveButton);
        }

        center.add(Box.createRigidArea(new Dimension(0, 10)));
        center.add(viewAllButton);
        center.add(Box.createRigidArea(new Dimension(0, 10)));
        center.add(logOutButton);

        panel.add(title, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAllEmployeePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel(new BorderLayout());

        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> {
                setSize(500, 400);
                cardLayout.show(mainPanel, "home");});
        topPanel.add(backButton, BorderLayout.WEST);

        JButton newEmployeeButton = new JButton("Add New Employee");
        newEmployeeButton.addActionListener(e -> {
            NewEmployeeRecord dialog = new NewEmployeeRecord(this);
            dialog.setVisible(true);
            EmployeeData newEmp = dialog.getNewEmployee();
            if (newEmp != null) {
                employeeManager.addEmployee(newEmp);
                try {
                    CSVHandler.appendEmployeeToCSV("src/MotorPH Employee Data - Employee Details.csv", newEmp);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving employee: " + ex.getMessage());
                    ex.printStackTrace();
                }
                JOptionPane.showMessageDialog(this, "Employee added!");
                refreshAllEmployeePanel();
            }
        });
        topPanel.add(newEmployeeButton, BorderLayout.EAST);

        JLabel title = new JLabel("All MotorPH Employee Records", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(title, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.add(createTableHeader());

        List<EmployeeData> employees = employeeManager.getAllEmployees();
        for (EmployeeData emp : employees) {
            tablePanel.add(createEmployeeRow(emp));
        }

        JScrollPane scrollPane = new JScrollPane(tablePanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTableHeader() {
        JPanel header = new JPanel(new GridLayout(1, 8));
        header.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
        String[] labels = { "Employee #", "Last Name", "First Name", "SSS #", "PhilHealth #", "TIN #", "Pag-IBIG #", "" };
        for (String label : labels) {
            JLabel lbl = new JLabel(label, SwingConstants.CENTER);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            header.add(lbl);
        }
        return header;
    }

    private JPanel createEmployeeRow(EmployeeData emp) {
        JPanel row = new JPanel(new GridLayout(1, 8));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        GovernmentDetails gov = emp.getGovernmentDetails();

        row.add(new JLabel(String.valueOf(emp.getEmployeeId())));
        row.add(new JLabel(emp.getLastName()));
        row.add(new JLabel(emp.getFirstName()));
        row.add(new JLabel(gov.getSssNumber()));
        row.add(new JLabel(gov.getPhilHealthNumber()));
        row.add(new JLabel(gov.getTinNumber()));
        row.add(new JLabel(gov.getPagIbigNumber()));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

        JButton viewButton = new JButton("View");
        viewButton.addActionListener(e -> showEmployeeDetails(emp));

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            NewEmployeeRecord dialog = new NewEmployeeRecord(this, emp);
            dialog.setVisible(true);
            EmployeeData updated = dialog.getNewEmployee();
            if (updated != null) {
                employeeManager.updateEmployee(updated.getEmployeeId(), updated);
                JOptionPane.showMessageDialog(this, "Employee updated!");
                refreshAllEmployeePanel();
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                employeeManager.deleteEmployee(emp.getEmployeeId());
                JOptionPane.showMessageDialog(this, "Employee deleted!");
                refreshAllEmployeePanel();
            }
        });
        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        row.add(buttonPanel);
        return row;
    }

    private void refreshAllEmployeePanel() {
        mainPanel.remove(mainPanel.getComponent(1));
        mainPanel.add(createAllEmployeePanel(), "allEmployees");
        cardLayout.show(mainPanel, "allEmployees");
    }

    private void showEmployeeDetails(EmployeeData emp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        CompensationDetails comp = emp.getCompensation();

        String details = String.format(
                "<html><body style='width: 400px;'>"
                        + "<b>ID:</b> %d<br>"
                        + "<b>Name:</b> %s<br>"
                        + "<b>Birthdate:</b> %s<br>"
                        + "<b>Address:</b> %s<br>"
                        + "<b>Phone:</b> %s<br>"
                        + "<b>Position:</b> %s<br>"
                        + "<b>Status:</b> %s<br>"
                        + "<b>Supervisor:</b> %s<br><br>"
                        + "<b>SSS:</b> %s<br>"
                        + "<b>PhilHealth:</b> %s<br>"
                        + "<b>Pag-IBIG:</b> %s<br>"
                        + "<b>TIN:</b> %s<br><br>"
                        + "<b>Basic Salary:</b> %s<br>"
                        + "<b>Rice Subsidy:</b> %s<br>"
                        + "<b>Phone Allowance:</b> %s<br>"
                        + "<b>Clothing Allowance:</b> %s<br>"
                        + "<b>Gross Semi-Monthly:</b> %s<br>"
                        + "<b>Hourly Rate:</b> %s<br>"
                        + "</body></html>",
                emp.getEmployeeId(), emp.getFullName(), dateFormat.format(emp.getBirthDate()), emp.getAddress(), emp.getPhoneNumber(),
                emp.getPosition(), emp.getStatus(), emp.getSupervisor(),
                emp.getGovernmentDetails().getSssNumber(), emp.getGovernmentDetails().getPhilHealthNumber(),
                emp.getGovernmentDetails().getPagIbigNumber(), emp.getGovernmentDetails().getTinNumber(),
                money.format(comp.getBasicSalary()), money.format(comp.getRiceSubsidy()),
                money.format(comp.getPhoneAllowance()), money.format(comp.getClothingAllowance()),
                money.format(comp.getGrossSemiMonthlyRate()), money.format(comp.getHourlyRate())
        );

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JLabel(details), BorderLayout.CENTER);

        JButton calcButton = new JButton("Calculate Monthly Salary");
        calcButton.addActionListener(e -> showMonthInputDialog(emp));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(calcButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        JOptionPane.showMessageDialog(this, panel, "Employee Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMonthInputDialog(EmployeeData emp) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] monthNames = new java.text.DateFormatSymbols().getMonths();
        JComboBox<String> monthCombo = new JComboBox<>(monthNames);
        JComboBox<Integer> yearCombo = new JComboBox<>();
        int currentYear = YearMonth.now().getYear();
        for (int y = 2024; y <= currentYear + 10; y++) {
            yearCombo.addItem(y);
        }
        panel.add(new JLabel("Select Month:"));
        panel.add(monthCombo);
        panel.add(new JLabel("Select Year:"));
        panel.add(yearCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Select Month and Year", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int monthIndex = monthCombo.getSelectedIndex() + 1;
            int year = (int) yearCombo.getSelectedItem();
            String monthFormatted = String.format("%02d-%d", monthIndex, year);
            if (monthExistsInAttendance(emp.getEmployeeId(), monthFormatted)) {
                PayrollCalc(emp, monthFormatted);
            } else {
                JOptionPane.showMessageDialog(this, "No attendance records found for " + monthFormatted);
            }
        }
    }

    private boolean monthExistsInAttendance(int employeeId, String month) {
        return new Attendance().hasAttendanceForMonth(employeeId, month);
    }


    public static void displayPayrollReport(PayrollReport reportPanel) {
        JScrollPane scrollPane = new JScrollPane(reportPanel);
        JOptionPane.showMessageDialog(null, scrollPane, "Payroll Report", JOptionPane.INFORMATION_MESSAGE);
    }


    private void showEmployeeInfoReadOnly(EmployeeData emp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        CompensationDetails comp = emp.getCompensation();

        String details = String.format(
                "<html><body style='width: 400px;'>"
                        + "<b>ID:</b> %d<br>"
                        + "<b>Name:</b> %s<br>"
                        + "<b>Birthdate:</b> %s<br>"
                        + "<b>Address:</b> %s<br>"
                        + "<b>Phone:</b> %s<br>"
                        + "<b>Position:</b> %s<br>"
                        + "<b>Status:</b> %s<br>"
                        + "<b>Supervisor:</b> %s<br><br>"
                        + "<b>SSS:</b> %s<br>"
                        + "<b>PhilHealth:</b> %s<br>"
                        + "<b>Pag-IBIG:</b> %s<br>"
                        + "<b>TIN:</b> %s<br><br>"
                        + "<b>Basic Salary:</b> %s<br>"
                        + "<b>Rice Subsidy:</b> %s<br>"
                        + "<b>Phone Allowance:</b> %s<br>"
                        + "<b>Clothing Allowance:</b> %s<br>"
                        + "<b>Gross Semi-Monthly:</b> %s<br>"
                        + "<b>Hourly Rate:</b> %s<br>"
                        + "</body></html>",
                emp.getEmployeeId(),
                emp.getFullName(),
                dateFormat.format(emp.getBirthDate()),
                emp.getAddress(),
                emp.getPhoneNumber(),
                emp.getPosition(),
                emp.getStatus(),
                emp.getSupervisor(),
                emp.getGovernmentDetails().getSssNumber(),
                emp.getGovernmentDetails().getPhilHealthNumber(),
                emp.getGovernmentDetails().getPagIbigNumber(),
                emp.getGovernmentDetails().getTinNumber(),
                money.format(comp.getBasicSalary()),
                money.format(comp.getRiceSubsidy()),
                money.format(comp.getPhoneAllowance()),
                money.format(comp.getClothingAllowance()),
                money.format(comp.getGrossSemiMonthlyRate()),
                money.format(comp.getHourlyRate())
        );

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(details), BorderLayout.CENTER);
        JOptionPane.showMessageDialog(this, panel, "Employee Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSearchEmployeeDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter Employee ID:");
        if (input != null && !input.trim().isEmpty()) {
            try {
                int empId = Integer.parseInt(input.trim());
                EmployeeData emp = employeeManager.findById(empId);
                if (emp != null) {
                    showEmployeeInfoReadOnly(emp);
                } else {
                    JOptionPane.showMessageDialog(this, "Employee not found.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Employee ID. Please enter a valid number.");
            }
        }
    }

    private void showSearchPayCoverageDialog() {

        String inputId = JOptionPane.showInputDialog(this, "Enter Employee ID:");
        if (inputId == null || inputId.trim().isEmpty()) return;

        try {
            int empId = Integer.parseInt(inputId.trim());
            EmployeeData emp = employeeManager.findById(empId);

            if (emp == null) {
                JOptionPane.showMessageDialog(this, "Employee not found.");
                return;
            }


            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            String[] monthNames = new java.text.DateFormatSymbols().getMonths();
            JComboBox<String> monthCombo = new JComboBox<>(monthNames);
            JComboBox<Integer> yearCombo = new JComboBox<>();
            int currentYear = YearMonth.now().getYear();
            for (int y = 2024; y <= currentYear + 10; y++) {
                yearCombo.addItem(y);
            }

            panel.add(new JLabel("Select Month:"));
            panel.add(monthCombo);
            panel.add(new JLabel("Select Year:"));
            panel.add(yearCombo);

            int result = JOptionPane.showConfirmDialog(this, panel, "Select Month and Year", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                int monthIndex = monthCombo.getSelectedIndex() + 1;
                int year = (int) yearCombo.getSelectedItem();
                String monthFormatted = String.format("%02d-%d", monthIndex, year);

                if (monthExistsInAttendance(emp.getEmployeeId(), monthFormatted)) {
                    PayrollCalc(emp, monthFormatted);
                } else {
                    JOptionPane.showMessageDialog(this, "No attendance records found for " + monthFormatted);
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid employee ID. Please enter a valin number.");
        }
    }

    private void showFileLeaveDialog() {

        EmployeeData loggedEmp = employeeManager.findByName(loggedInUsername);

        if (loggedEmp == null) {
            List<EmployeeData> allEmps = employeeManager.getAllEmployees();
            for (EmployeeData emp : allEmps) {
                String empName = emp.getFullName().toLowerCase();
                String loginName = loggedInUsername.toLowerCase();


                if (empName.contains(loginName) || loginName.contains(empName)) {
                    loggedEmp = emp;
                    break;
                }
            }
        }

        if (loggedEmp == null) {

            System.out.println("DEBUG: Could not find employee record for user: " + loggedInUsername);
            System.out.println("Available Employees in System:");
            for (EmployeeData e : employeeManager.getAllEmployees()) {
                System.out.println(" - " + e.getFullName());
            }

            JOptionPane.showMessageDialog(this, "Employee record not found for user: " + loggedInUsername);
            return;
        }


        int empId = loggedEmp.getEmployeeId();


        JTextField empIdField = new JTextField();
        empIdField.setEditable(false);
        empIdField.setText(String.valueOf(empId));
        JTextField typeField = new JTextField(10);


        String[] months = new DateFormatSymbols().getMonths();
        JComboBox<String> startMonth = new JComboBox<>(months);
        JComboBox<Integer> startDay = new JComboBox<>();
        JComboBox<Integer> startYear = new JComboBox<>();

        JComboBox<String> endMonth = new JComboBox<>(months);
        JComboBox<Integer> endDay = new JComboBox<>();
        JComboBox<Integer> endYear = new JComboBox<>();

        for (int d = 1; d <= 31; d++) {
            startDay.addItem(d);
            endDay.addItem(d);
        }

        int currentYear = Year.now().getValue();
        for (int y = currentYear; y <= currentYear + 2; y++) {
            startYear.addItem(y);
            endYear.addItem(y);
        }

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Employee ID:")); panel.add(empIdField);
        panel.add(new JLabel("Leave Type:")); panel.add(typeField);


        panel.add(new JLabel("Start Date:"));
        JPanel startPanel = new JPanel();
        startPanel.add(startMonth);
        startPanel.add(startDay);
        startPanel.add(startYear);
        panel.add(startPanel);

        panel.add(new JLabel("End Date:"));
        JPanel endPanel = new JPanel();
        endPanel.add(endMonth);
        endPanel.add(endDay);
        endPanel.add(endYear);
        panel.add(endPanel);

        int result = JOptionPane.showConfirmDialog(this, panel, "File Leave", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                LeaveService service = new LeaveService();
                String leaveId = "L" + System.currentTimeMillis();


                String startDate = String.format("%04d-%02d-%02d",
                        startYear.getSelectedItem(),
                        startMonth.getSelectedIndex() + 1,
                        startDay.getSelectedItem());

                String endDate = String.format("%04d-%02d-%02d",
                        endYear.getSelectedItem(),
                        endMonth.getSelectedIndex() + 1,
                        endDay.getSelectedItem());

                LeaveManagement leave = new LeaveManagement(
                        leaveId,
                        empIdField.getText(),
                        typeField.getText(),
                        startDate,
                        endDate
                );

                service.fileLeave(leave);
                JOptionPane.showMessageDialog(this, "Leave filed successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void showLeaveManagementDialog() {
        try {
            LeaveService service = new LeaveService();
            List<LeaveManagement> leaves = service.loadAllLeaves();

            String[] columnNames = {"Leave ID", "Employee ID", "Type", "Start", "End", "Status"};
            String[][] data = new String[leaves.size()][6];

            for (int i = 0; i < leaves.size(); i++) {
                LeaveManagement l = leaves.get(i);
                data[i][0] = l.getLeaveId();
                data[i][1] = l.getEmployeeId();
                data[i][2] = l.getLeaveType();
                data[i][3] = l.getStartDate();
                data[i][4] = l.getEndDate();
                data[i][5] = l.getStatus().toString();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(700, 300));

            int result = JOptionPane.showConfirmDialog(this, scrollPane, "Leave Requests",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select a leave request first.");
                    showLeaveManagementDialog();
                    return;
                }

                String leaveId = table.getValueAt(row, 0).toString();

                String[] options = {"Approve", "Reject"};
                int choice = JOptionPane.showOptionDialog(this, "Choose action", "Leave Action",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                if (choice == 0) {
                    service.approveLeave(leaveId);
                    JOptionPane.showMessageDialog(this, "Leave approved.");
                    showLeaveManagementDialog();
                } else if (choice == 1) {
                    service.rejectLeave(leaveId);
                    JOptionPane.showMessageDialog(this, "Leave rejected.");
                    showLeaveManagementDialog();
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private JPanel createEmployeeDashboard() {
        EmployeeData currentEmp = employeeManager.findByName(loggedInUsername);

        if (currentEmp == null) {
            List<EmployeeData> allEmps = employeeManager.getAllEmployees();
            for (EmployeeData emp : allEmps) {
                String empName = emp.getFullName().toLowerCase();
                String loginName = loggedInUsername.toLowerCase();

                if (empName.contains(loginName) || loginName.contains(empName)) {
                    currentEmp = emp;
                    break;
                }
            }
        }

        if (currentEmp == null) {
            System.out.println("DEBUG: Could not find employee record for user: " + loggedInUsername);
            System.out.println("Available Employees in System:");
            for (EmployeeData e : employeeManager.getAllEmployees()) {
                System.out.println(" - " + e.getFullName());
            }
            JOptionPane.showMessageDialog(this, "Employee record not found for user: " + loggedInUsername);
        }

        final EmployeeData finalEmp = currentEmp;

        String EmpfullName = (currentEmp != null) ? currentEmp.getFullName() : "User not found";
        String EmpID = (currentEmp != null) ? String.valueOf(currentEmp.getEmployeeId()) : "N/A";
        String EmpPosition = (currentEmp != null) ? currentEmp.getPosition() : "N/A";
        String EmpAddress = (currentEmp != null) ? currentEmp.getAddress() : "N/A";

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        mainContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel nameLabel = new JLabel("  Full Name: " + EmpfullName);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        nameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        nameLabel.setPreferredSize(new Dimension(500, 40));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        mainContainer.add(nameLabel);

        mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel idLabel = new JLabel("  ID: " + EmpID);
        idLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        idLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        idLabel.setPreferredSize(new Dimension(500, 40));
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        idLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        mainContainer.add(idLabel);

        mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel posLabel = new JLabel("  Position: " + EmpPosition);
        posLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        posLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        posLabel.setPreferredSize(new Dimension(500, 40));
        posLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        posLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        mainContainer.add(posLabel);

        mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel addrLabel = new JLabel("  Address: " + EmpAddress);
        addrLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        addrLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addrLabel.setPreferredSize(new Dimension(500, 40));
        addrLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addrLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        mainContainer.add(addrLabel);

        mainContainer.add(Box.createRigidArea(new Dimension(0, 40)));

        JButton viewDetailsBtn = new JButton("View all my details");
        viewDetailsBtn.setFocusPainted(false);
        viewDetailsBtn.setFont(new Font("Arial", Font.PLAIN, 15));
        viewDetailsBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        viewDetailsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        viewDetailsBtn.addActionListener(e -> {
            if (finalEmp != null) {
                showEmployeeInfoReadOnly(finalEmp);
            }
        });
        mainContainer.add(viewDetailsBtn);

        mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton calcPayrollBtn = new JButton("Calculate Payroll");
        calcPayrollBtn.setFocusPainted(false);
        calcPayrollBtn.setFont(new Font("Arial", Font.PLAIN, 15));
        calcPayrollBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        calcPayrollBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        calcPayrollBtn.addActionListener(e -> {
            if (finalEmp != null) showMonthInputDialog(finalEmp);
        });
        mainContainer.add(calcPayrollBtn);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton fileLeaveBtn = new JButton("File Leave");
        fileLeaveBtn.setFocusPainted(false);
        fileLeaveBtn.setFont(new Font("Arial", Font.PLAIN, 15));
        fileLeaveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        fileLeaveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        fileLeaveBtn.addActionListener(e -> showFileLeaveDialog());
        mainContainer.add(fileLeaveBtn);

    mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));

    JButton viewMyLeavesBtn = new JButton("View My Leave Status");
    viewMyLeavesBtn.setFocusPainted(false);
    viewMyLeavesBtn.setFont(new Font("Arial", Font.PLAIN, 15));
    viewMyLeavesBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
    viewMyLeavesBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
    viewMyLeavesBtn.addActionListener(e -> showMyLeaveStatusDialog());
    mainContainer.add(viewMyLeavesBtn);

    mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton logOutBtn = new JButton("Log Out");
        logOutBtn.setFocusPainted(false);
        logOutBtn.setFont(new Font("Arial", Font.PLAIN, 15));
        logOutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        logOutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logOutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        mainContainer.add(logOutBtn);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        return mainContainer;
    }


    private void showMyLeaveStatusDialog() {
        try {
            EmployeeData loggedEmp = employeeManager.findByName(loggedInUsername);

            if (loggedEmp == null) {
                List<EmployeeData> allEmps = employeeManager.getAllEmployees();
                for (EmployeeData emp : allEmps) {
                    String empName = emp.getFullName().toLowerCase();
                    String loginName = loggedInUsername.toLowerCase();

                    if (empName.contains(loginName) || loginName.contains(empName)) {
                        loggedEmp = emp;
                        break;
                    }
                }
            }

            if (loggedEmp == null) {
                System.out.println("DEBUG: Could not find employee record for user: " + loggedInUsername);
                System.out.println("Available Employees in System:");
                for (EmployeeData e : employeeManager.getAllEmployees()) {
                    System.out.println(" - " + e.getFullName());
                }
                JOptionPane.showMessageDialog(this, "Employee record not found for user: " + loggedInUsername);
                return;
            }

            String empId = String.valueOf(loggedEmp.getEmployeeId());

            LeaveService service = new LeaveService();
            List<LeaveManagement> leaves = service.loadAllLeaves();

            List<LeaveManagement> myLeaves = new java.util.ArrayList<>();
            for (LeaveManagement l : leaves) {
                if (l.getEmployeeId().equals(empId)) {
                    myLeaves.add(l);
                }
            }

            if (myLeaves.isEmpty()) {
                JOptionPane.showMessageDialog(this, "You have no leave records.");
                return;
            }

            String[] columnNames = {"Leave ID", "Type", "Start", "End", "Status"};
            String[][] data = new String[myLeaves.size()][5];

            for (int i = 0; i < myLeaves.size(); i++) {
                LeaveManagement l = myLeaves.get(i);
                data[i][0] = l.getLeaveId();
                data[i][1] = l.getLeaveType();
                data[i][2] = l.getStartDate();
                data[i][3] = l.getEndDate();
                data[i][4] = l.getStatus().toString();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(600, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "My Leave Status", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}