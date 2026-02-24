package com.mycompany.hw2.model;

import java.util.Date;

public class ProbationaryEmployee extends EmployeeData {

    public ProbationaryEmployee(int employeeId, String firstName, String lastName, Date birthDate,
                                String address, String phoneNumber, String status, String position,
                                String supervisor, CompensationDetails compensation,
                                GovernmentDetails governmentDetails) {
        super(employeeId, firstName, lastName, birthDate, address, phoneNumber,
                status, position, supervisor, compensation, governmentDetails);
    }

    @Override
    public double calculateNetPay(double regularHours, double overtimeHours) {

        CompensationDetails comp = getCompensation();

        double hourlyRate = comp.getHourlyRate();

        double grossPay =
                (regularHours * hourlyRate) +
                        (overtimeHours * hourlyRate * 1.25);

        double benefits = getBenefits();

        double sss = Deductions.calculateSSS(grossPay);
        double philHealth = Deductions.calculatePhilHealth(grossPay);
        double pagIbig = Deductions.calculatePagIbig(grossPay);
        double tax = Deductions.calculateWithholdingTax(grossPay, hourlyRate);

        return grossPay + benefits - (sss + philHealth + pagIbig + tax);
    }
}