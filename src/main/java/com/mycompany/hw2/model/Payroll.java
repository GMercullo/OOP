package com.mycompany.hw2.model;

import com.mycompany.hw2.PayrollReport;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import javax.swing.*;

public class Payroll {
    private static final DecimalFormat hoursFormat = new DecimalFormat("#0.##");
    private static final DecimalFormat money = new DecimalFormat("\u20b1#,##0.00");

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
            double grossSalary = GrossWage.calculateGross(hourlyRate, regularHours, overtimeHours);

            double sss = Deductions.calculateSSS(grossSalary);
            double philHealth = Deductions.calculatePhilHealth(grossSalary);
            double pagIbig = Deductions.calculatePagIbig(grossSalary);
            double tax = Deductions.calculateWithholdingTax(grossSalary, hourlyRate);

            double net = grossSalary - (sss + philHealth + pagIbig + tax);

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
            summary.setValue("Month:", monthYear.format(outputFormatter));

            summary.setValue("Regular Hours:", hoursFormat.format(regularHours));
            summary.setValue("Overtime Hours:", hoursFormat.format(overtimeHours));

            summary.setValue("Hourly Rate:", money.format(hourlyRate));
            summary.setValue("Gross Salary:", money.format(grossSalary));

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
