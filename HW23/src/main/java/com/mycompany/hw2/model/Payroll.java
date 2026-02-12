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

    public static void processMonthlyPayroll(EmployeeRepository repository, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        Attendance attendance = new Attendance();

        for (EmployeeData emp : repository.getAllEmployees()) {
            Attendance.HoursWorked hours = attendance.calculateHours(emp.getEmployeeId(), start, end);

            double net = emp.calculateNetPay(hours.getRegularHours(), hours.getOvertimeHours());

            double hourlyRate = GrossWage.calculateHourlyRate(emp.getCompensation().getBasicSalary());
            double gross = GrossWage.calculateGross(hourlyRate, hours.getRegularHours(), hours.getOvertimeHours());

            double sss = Deductions.calculateSSS(gross);
            double phil = Deductions.calculatePhilHealth(gross);
            double pagibig = Deductions.calculatePagIbig(gross);
            double tax = Deductions.calculateWithholdingTax(emp.getCompensation().getBasicSalary(), hourlyRate);

            PayrollReport reportPanel = new PayrollReport();
            reportPanel.setValue("Employee #:", String.valueOf(emp.getEmployeeId()));
            reportPanel.setValue("Last Name:", emp.getLastName());
            reportPanel.setValue("First Name:", emp.getFirstName());
            reportPanel.setValue("Birth Date:", new SimpleDateFormat("MM/dd/yyyy").format(emp.getBirthDate()));
            reportPanel.setValue("Address:", emp.getAddress());
            reportPanel.setValue("Phone #:", emp.getPhoneNumber());
            reportPanel.setValue("Position:", emp.getPosition());
            reportPanel.setValue("Status:", emp.getStatus());
            reportPanel.setValue("Supervisor:", emp.getSupervisor());

            reportPanel.setValue("SSS #:", emp.getGovernmentDetails().getSssNumber());
            reportPanel.setValue("PhilHealth #:", emp.getGovernmentDetails().getPhilHealthNumber());
            reportPanel.setValue("TIN #:", emp.getGovernmentDetails().getTinNumber());
            reportPanel.setValue("Pag-IBIG #:", emp.getGovernmentDetails().getPagIbigNumber());

            reportPanel.setValue("Basic Salary:", String.format("%.2f", emp.getCompensation().getBasicSalary()));
            reportPanel.setValue("Rice Subsidy:", String.format("%.2f", emp.getCompensation().getRiceSubsidy()));
            reportPanel.setValue("Phone Subsidy:", String.format("%.2f", emp.getCompensation().getPhoneAllowance()));
            reportPanel.setValue("Clothing Allowance:", String.format("%.2f", emp.getCompensation().getClothingAllowance()));

            reportPanel.setValue("Gross Semi-Monthly Salary:", String.format("%.2f", emp.getCompensation().getGrossSemiMonthlyRate()));
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

            double hourlyRate = GrossWage.calculateHourlyRate(empData.getCompensation().getBasicSalary());
            double gross = GrossWage.calculateGross(hourlyRate, regularHours, overtimeHours);

            double sss = Deductions.calculateSSS(gross);
            double philHealth = Deductions.calculatePhilHealth(gross);
            double pagIbig = Deductions.calculatePagIbig(gross);
            double tax = Deductions.calculateWithholdingTax(gross, hourlyRate);

            double net = empData.calculateNetPay(regularHours, overtimeHours);

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
            summary.setValue("Rice Subsidy:", money.format(empData.getCompensation().getRiceSubsidy()));
            summary.setValue("Phone Subsidy:", money.format(empData.getCompensation().getPhoneAllowance()));
            summary.setValue("Clothing Allowance:", money.format(empData.getCompensation().getClothingAllowance()));
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
