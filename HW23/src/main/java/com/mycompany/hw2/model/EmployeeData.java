package com.mycompany.hw2.model;

import java.util.Date;

public abstract class EmployeeData {

    private int employeeId;

    private String firstName;
    private String lastName;
    private Date birthDate;
    private String address;
    private String phoneNumber;

    private String status;
    private String position;
    private String supervisor;

    private CompensationDetails compensation;
    private GovernmentDetails governmentDetails;

    public EmployeeData(
            int employeeId, String firstName, String lastName, Date birthDate, String address, String phoneNumber, String status, String position, String departmentSupervisor, CompensationDetails compensation, GovernmentDetails governmentDetails) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.position = position;
        this.supervisor = departmentSupervisor;
        this.compensation = compensation;
        this.governmentDetails = governmentDetails;
    }

    public int getEmployeeId() { return employeeId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public Date getBirthDate() { return birthDate; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getStatus() { return status; }
    public String getPosition() { return position; }
    public String getSupervisor() { return supervisor; }

    public CompensationDetails getCompensation() { return compensation; }

    public GovernmentDetails getGovernmentDetails() { return governmentDetails; }

    public void setFirstName(String firstName) {
        if (firstName != null && !firstName.trim().isEmpty()) {
            this.firstName = firstName;
        }
    }

    public void setLastName(String lastName) {
        if (lastName != null && !lastName.trim().isEmpty()) {
            this.lastName = lastName;
        }
    }

    public void setBirthDate(Date birthDate) {
        if (birthDate != null) {
            this.birthDate = birthDate;
        }
    }

    public void setAddress(String address) {
        if (address != null && !address.trim().isEmpty()) {
            this.address = address;
        }
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            this.phoneNumber = phoneNumber;
        }
    }

    public void setStatus(String status) {
        if (status != null && !status.trim().isEmpty()) {
            this.status = status;
        }
    }

    public void setPosition(String position) {
        if (position != null && !position.trim().isEmpty()) {
            this.position = position;
        }
    }

    public void setSupervisor(String supervisor) {
        if (supervisor != null && !supervisor.trim().isEmpty()) {
            this.supervisor = supervisor;
        }
    }

    public void setCompensation(CompensationDetails compensation) {
        if (compensation != null) {
            this.compensation = compensation;
        }
    }

    public void setGovernmentDetails(GovernmentDetails governmentDetails) {
        if (governmentDetails != null) {
            this.governmentDetails = governmentDetails;
        }
    }

    public double calculateNetPay(double regularHours, double overtimeHours) {
        double hourlyRate = GrossWage.calculateHourlyRate(this.compensation.getBasicSalary());
        double gross = GrossWage.calculateGross(hourlyRate, regularHours, overtimeHours);

        double sss = Deductions.calculateSSS(gross);
        double philHealth = Deductions.calculatePhilHealth(gross);
        double pagIbig = Deductions.calculatePagIbig(gross);
        double tax = Deductions.calculateWithholdingTax(gross, hourlyRate);

        double totalDeductions = sss + philHealth + pagIbig + tax;

        return gross - totalDeductions + getBenefits();
    }

    protected double getBenefits() {
        return 0;
    }

}