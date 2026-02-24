package com.mycompany.hw2.model;

/**
 * Represents the government and mandatory deductions from an employee's salary.
 *
 * Author: gmmercullo
 */
public class Deductions {

    private Deductions() {

    }

    public static double calculateSSS(double monthlySalary) {
    double calculateSSS = 0;

    if (monthlySalary < 3250){
        calculateSSS = 135;
    } else if (monthlySalary <= 3750){
        calculateSSS = 157.50;
    } else if (monthlySalary <= 4250){
        calculateSSS = 180;
    } else if (monthlySalary <= 4750){
        calculateSSS = 202.50;
    } else if (monthlySalary <= 5250){
        calculateSSS = 225;
    } else if (monthlySalary <= 5750){
        calculateSSS = 247.50;
    } else if (monthlySalary <= 6250){
        calculateSSS = 270;
    } else if (monthlySalary <= 6750){
        calculateSSS = 292.50;
    } else if (monthlySalary <= 7250){
        calculateSSS = 315;
    } else if (monthlySalary <= 7750){
        calculateSSS = 337.50;
    } else if (monthlySalary <= 8250){
        calculateSSS = 360;
    } else if (monthlySalary <= 8750){
        calculateSSS = 382.50;
    } else if (monthlySalary <= 9250){
        calculateSSS = 405;
    } else if (monthlySalary <= 9750){
        calculateSSS = 427.50;
    } else if (monthlySalary <= 10250){
        calculateSSS = 450;
    } else if (monthlySalary <= 10750){
        calculateSSS = 472.50;
    } else if (monthlySalary <= 11250){
        calculateSSS = 495;
    } else if (monthlySalary <= 11750){
        calculateSSS = 517.50;
    } else if (monthlySalary <= 12250){
        calculateSSS = 540;
    } else if (monthlySalary <= 12750){
        calculateSSS = 562.50;
    } else if (monthlySalary <= 13250){
        calculateSSS = 585;
    } else if (monthlySalary <= 13750){
        calculateSSS = 607.50;
    } else if (monthlySalary <= 14250){
        calculateSSS = 630;
    } else if (monthlySalary <= 14750){
        calculateSSS = 652.50;
    } else if (monthlySalary <= 15250){
        calculateSSS = 675;
    } else if (monthlySalary <= 15750){
        calculateSSS = 697.50;
    } else if (monthlySalary <= 16250){
        calculateSSS = 720;
    } else if (monthlySalary <= 16750){
        calculateSSS = 742.50;
    } else if (monthlySalary <= 17250){
        calculateSSS = 765;
    } else if (monthlySalary <= 17750){
        calculateSSS = 787.50;
    } else if (monthlySalary <= 18250){
        calculateSSS = 810;
    } else if (monthlySalary <= 18750){
        calculateSSS = 832.50;
    } else if (monthlySalary <= 19250){
        calculateSSS = 855;
    } else if (monthlySalary <= 19750){
        calculateSSS = 877.50;
    } else if (monthlySalary <= 20250){
        calculateSSS = 900;
    } else if (monthlySalary <= 20750){
        calculateSSS = 922.50;
    } else if (monthlySalary <= 21250){
        calculateSSS = 945;
    } else if (monthlySalary <= 21750){
        calculateSSS = 967.50;
    } else if (monthlySalary <= 22250){
        calculateSSS = 990;
    } else if (monthlySalary <= 22750){
        calculateSSS = 1012.50;
    } else if (monthlySalary <= 23250){
        calculateSSS = 1035;
    } else if (monthlySalary <= 23750){
        calculateSSS = 1057.50;
    } else if (monthlySalary <= 24250){
        calculateSSS = 1080;
    } else if (monthlySalary <= 24750){
        calculateSSS = 1102.50;
    } else {
        calculateSSS = 1125;
    }

    return calculateSSS;
}

    public static double calculatePhilHealth(double monthlySalary) {
    double premium = (monthlySalary <= 10000) ? 300 :
                     (monthlySalary >= 60000) ? 1800 :
                     monthlySalary * 0.03;
    return premium / 2; // employee share
}
    public static double calculatePagIbig(double monthlySalary) {
        double contributionRate = (monthlySalary <= 1500) ? 0.01 : 0.02;
        return monthlySalary * contributionRate;
    }

    public static double calculateWithholdingTax(double monthlySalary, double hourlyRate) {
        double taxableIncome = monthlySalary;
        double calculatedTax = 0;
        if (taxableIncome <= 20833) {
            calculatedTax = 0;
        } else if (taxableIncome <= 33332) {
            calculatedTax = (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome <= 66666) {
            calculatedTax = 2500 + (taxableIncome - 33332) * 0.25;
        } else if (taxableIncome <= 166666) {
            calculatedTax = 10833.33 + (taxableIncome - 66666) * 0.30;
        } else if (taxableIncome <= 666666) {
            calculatedTax = 40833.33 + (taxableIncome - 166666) * 0.32;
        } else {
            calculatedTax = 200833.33 + (taxableIncome - 666666) * 0.35;
        }   return calculatedTax;
    }

    public static double getTotalDeduction(double sss, double philHealth, double pagIbig, double tax) {
        return sss + philHealth + pagIbig + tax;
    }
}
