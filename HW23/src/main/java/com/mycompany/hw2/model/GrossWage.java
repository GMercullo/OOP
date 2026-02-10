package com.mycompany.hw2.model;

/**
 * Represents the gross wage computation for an employee
 * based on hours worked and hourly rate.
 *
 * Author: gmmercullo
 */
public class GrossWage {
    private GrossWage() {

    }
    private static final int WORK_DAYS_PER_MONTH = 21;
    private static final int HOURS_PER_DAY = 8;
    private static final double OVERTIME_RATE_MULTIPLIER = 1.25;

    public static double calculateHourlyRate(double monthlySalary) {
        return (monthlySalary / WORK_DAYS_PER_MONTH) / HOURS_PER_DAY;
    }

    public static double calculateGross(double hourlyRate, double regHours, double otHours) {
        double regular = hourlyRate * regHours;
        double overtime = hourlyRate * otHours * OVERTIME_RATE_MULTIPLIER;
        return regular + overtime;
    }
}

