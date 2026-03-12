package com.mycompany.hw2.model;

/**
 * Stores employee compensation details and enforces valid salary-related updates.
 * This class protects sensitive pay information through encapsulation.
 */
public class CompensationDetails {
    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double grossSemiMonthlyRate;
    private double hourlyRate;

    public CompensationDetails(double basicSalary, double riceSubsidy, double phoneAllowance,
                               double clothingAllowance, double grossSemiMonthlyRate, double hourlyRate) {

        if (basicSalary < 0 || hourlyRate <= 0) {
            throw new IllegalArgumentException("Salary values must be non-negative and hourly rate must be greater than zero.");
        }

        this.basicSalary = basicSalary;
        this.riceSubsidy = Math.max(0, riceSubsidy);
        this.phoneAllowance = Math.max(0, phoneAllowance);
        this.clothingAllowance = Math.max(0, clothingAllowance);
        this.grossSemiMonthlyRate = Math.max(0, grossSemiMonthlyRate);
        this.hourlyRate = hourlyRate;
    }

    // Getters and setters for each field
    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) {
        if (basicSalary >= 0) {
            this.basicSalary = basicSalary;
        }
    }

    public double getRiceSubsidy() { return riceSubsidy; }
    public void setRiceSubsidy(double riceSubsidy) {
        if (riceSubsidy >= 0) {
            this.riceSubsidy = riceSubsidy;
        }
    }

    public double getPhoneAllowance() { return phoneAllowance; }
    public void setPhoneAllowance(double phoneAllowance) {
        if (phoneAllowance >= 0) {
            this.phoneAllowance = phoneAllowance;
        }
    }

    public double getClothingAllowance() { return clothingAllowance; }
    public void setClothingAllowance(double clothingAllowance) {
        if (clothingAllowance >= 0) {
            this.clothingAllowance = clothingAllowance;
        }
    }

    public double getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
    public void setGrossSemiMonthlyRate(double grossSemiMonthlyRate) {
        if (grossSemiMonthlyRate >= 0) {
            this.grossSemiMonthlyRate = grossSemiMonthlyRate;
        }
    }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) {
        if (hourlyRate > 0) {
            this.hourlyRate = hourlyRate;
        }
    }
}
