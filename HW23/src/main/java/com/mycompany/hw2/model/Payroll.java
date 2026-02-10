package com.mycompany.hw2.model;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import com.mycompany.hw2.HW2;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import javax.swing.*;

public class Payroll {
    private static final DecimalFormat hoursFormat = new DecimalFormat("#0.##");
    private static final DecimalFormat money = new DecimalFormat("\u20b1#,##0.00");

    private static final String EMPLOYEE_CSV_FILE = "src/MotorPH Employee Data - Employee Details.csv";
    private static final Logger logger = Logger.getLogger(Payroll.class.getName());

    public static void processMonthlyPayroll(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        try (CSVReader reader = new CSVReader(new FileReader(EMPLOYEE_CSV_FILE))) {
            String[] row;
            boolean isFirstLine = true;

            while ((row = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                if (row.length < 17) {
                    logger.warning("Invalid employee data row (expected at least 17 columns): " + String.join(",", row));
                    continue;
                }

                // Parse employee data from columns
                String empNo = row[0].trim();
                String lastName = row[1].trim();
                String firstName = row[2].trim();
                String birthDate = row[3].trim();
                String address = row[4].trim();
                String phone = row[5].trim();
                String position = row[6].trim();
                String status = row[7].trim();
                String supervisor = row[8].trim();

                String sssNumber = row[9].trim();
                String philHealthNumber = row[10].trim();
                String tinNumber = row[11].trim();
                String pagIbigNumber = row[12].trim();

                double monthlySalary = CSVHandler.parseDouble(row[13]);
                double riceSubsidy = CSVHandler.parseDouble(row[14]);
                double phoneSubsidy = CSVHandler.parseDouble(row[15]);
                double clothingAllowance = CSVHandler.parseDouble(row[16]);

                double semiMonthly = monthlySalary / 2;
                double hourlyRate = GrossWage.calculateHourlyRate(monthlySalary);

                int empId;
                try {
                    empId = Integer.parseInt(empNo);
                } catch (NumberFormatException e) {
                    logger.warning("Invalid employee ID: " + empNo);
                    continue;
                }

                // Attendance calculation
                Attendance attendance = new Attendance();
                Attendance.HoursWorked hours = attendance.calculateHours(empId, start, end);

                // Gross wage calculation
                double gross = GrossWage.calculateGross(hourlyRate, hours.getRegularHours(), hours.getOvertimeHours());

                // Deductions
                double sss = Deductions.calculateSSS(gross);
                double phil = Deductions.calculatePhilHealth(gross);
                double pagibig = Deductions.calculatePagIbig(gross);
                double tax = Deductions.calculateWithholdingTax(monthlySalary, hourlyRate);

                double net = gross - Deductions.getTotalDeduction(sss, phil, pagibig, tax)
                        + riceSubsidy + phoneSubsidy + clothingAllowance;

                // GUI Payroll Report
                PayrollReport reportPanel = new PayrollReport();
                reportPanel.setValue("Employee #:", empNo);
                reportPanel.setValue("Last Name:", lastName);
                reportPanel.setValue("First Name:", firstName);
                reportPanel.setValue("Birth Date:", birthDate);
                reportPanel.setValue("Address:", address);
                reportPanel.setValue("Phone #:", phone);
                reportPanel.setValue("Position:", position);
                reportPanel.setValue("Status:", status);
                reportPanel.setValue("Supervisor:", supervisor);

                reportPanel.setValue("SSS #:", sssNumber);
                reportPanel.setValue("PhilHealth #:", philHealthNumber);
                reportPanel.setValue("TIN #:", tinNumber);
                reportPanel.setValue("Pag-IBIG #:", pagIbigNumber);

                reportPanel.setValue("Basic Salary:", String.format("%.2f", monthlySalary));
                reportPanel.setValue("Rice Subsidy:", String.format("%.2f", riceSubsidy));
                reportPanel.setValue("Phone Subsidy:", String.format("%.2f", phoneSubsidy));
                reportPanel.setValue("Clothing Allowance:", String.format("%.2f", clothingAllowance));

                reportPanel.setValue("Gross Semi-Monthly Salary:", String.format("%.2f", semiMonthly));
                reportPanel.setValue("Hourly Rate:", String.format("%.2f", hourlyRate));
                reportPanel.setValue("Month:", start.getMonth().toString() + " " + year);

                reportPanel.setValue("Regular Hours:", String.format("%.2f", hours.getRegularHours()));
                reportPanel.setValue("Overtime Hours:", String.format("%.2f", hours.getOvertimeHours()));
                reportPanel.setValue("Gross Salary:", String.format("%.2f", gross));

                reportPanel.setValue("SSS Deduction:", String.format("%.2f", sss));
                reportPanel.setValue("PhilHealth Deduction:", String.format("%.2f", phil));
                reportPanel.setValue("Pag-IBIG Deduction:", String.format("%.2f", pagibig));
                reportPanel.setValue("Withholding Tax:", String.format("%.2f", tax));

                reportPanel.setValue("Net Salary:", String.format("%.2f", net));

                HW2.displayPayrollReport(reportPanel);
            }

        } catch (IOException | CsvValidationException e) {
            logger.severe("Error reading employee CSV file: " + e.getMessage());
        }
    }

    public static void PayrollCalc(EmployeeData empData, String monthInput) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM-yyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            YearMonth monthYear = YearMonth.parse(monthInput, inputFormatter);
            LocalDate start = monthYear.atDay(1);
            LocalDate end = monthYear.atEndOfMonth();

            Attendance attendance = new Attendance();
            double regularHours = attendance.getTotalRegularHours(empData.getEmployeeId(), start, end);
            double overtimeHours = attendance.getTotalOvertimeHours(empData.getEmployeeId(), start, end);

            // Gross wage logic
            double hourlyRate = GrossWage.calculateHourlyRate(empData.getCompensation().getBasicSalary());
            double gross = GrossWage.calculateGross(hourlyRate, regularHours, overtimeHours);

            // Deductions logic (Still needed for the display report)
            double sss = Deductions.calculateSSS(gross);
            double philHealth = Deductions.calculatePhilHealth(gross);
            double pagIbig = Deductions.calculatePagIbig(gross);
            double tax = Deductions.calculateWithholdingTax(gross, hourlyRate);

            // --- POLYMORPHISM IN ACTION ---
            // Instead of manually adding (rice + phone + clothing) here, we ask the object to do it.
            // If empData is Regular, it adds benefits. If Contractual, it adds 0.
            double net = empData.calculateNetPay(regularHours, overtimeHours);
            // ------------------------------

            double rice = empData.getCompensation().getRiceSubsidy();
            double phone = empData.getCompensation().getPhoneAllowance();
            double clothing = empData.getCompensation().getClothingAllowance();

            PayrollReport summary = new PayrollReport();
            summary.setValue("Employee #:", String.valueOf(empData.getEmployeeId()));
            summary.setValue("Last Name:", empData.getLastName());
            summary.setValue("First Name:", empData.getFirstName());
            summary.setValue("Birth Date:", new SimpleDateFormat("MMM dd, yyyy").format(empData.getBirthDate()));
            summary.setValue("Address:", empData.getAddress());
            summary.setValue("Phone #:", empData.getPhoneNumber());
            summary.setValue("Position:", empData.getPosition());
            summary.setValue("Status:", empData.getStatus());
            summary.setValue("Supervisor:", empData.getSupervisor());

            summary.setValue("SSS #:", empData.getGovernmentDetails().getSssNumber());
            summary.setValue("PhilHealth #:", empData.getGovernmentDetails().getPhilHealthNumber());
            summary.setValue("TIN #:", empData.getGovernmentDetails().getTinNumber());
            summary.setValue("Pag-IBIG #:", empData.getGovernmentDetails().getPagIbigNumber());

            summary.setValue("Basic Salary:", money.format(empData.getCompensation().getBasicSalary()));
            summary.setValue("Rice Subsidy:", money.format(rice));
            summary.setValue("Phone Subsidy:", money.format(phone));
            summary.setValue("Clothing Allowance:", money.format(clothing));
            summary.setValue("Gross Semi-Monthly Salary:", money.format(empData.getCompensation().getGrossSemiMonthlyRate()));
            summary.setValue("Hourly Rate:", money.format(hourlyRate));
            summary.setValue("Month:", monthYear.format(outputFormatter));

            summary.setValue("Regular Hours:", hoursFormat.format(regularHours));
            summary.setValue("Overtime Hours:", hoursFormat.format(overtimeHours));
            summary.setValue("Gross Salary:", money.format(gross));

            summary.setValue("SSS Deduction:", money.format(sss));
            summary.setValue("PhilHealth Deduction:", money.format(philHealth));
            summary.setValue("Pag-IBIG Deduction:", money.format(pagIbig));
            summary.setValue("Withholding Tax:", money.format(tax));

            summary.setValue("Net Salary:", money.format(net));

            JScrollPane scrollPane = new JScrollPane(summary);
            scrollPane.setPreferredSize(new Dimension(600, 700));
            JOptionPane.showMessageDialog(null, scrollPane, "Payroll Summary", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}