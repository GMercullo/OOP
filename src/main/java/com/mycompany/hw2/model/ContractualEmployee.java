package com.mycompany.hw2.model;

import java.util.Date;

public class ContractualEmployee extends EmployeeData {

    public ContractualEmployee(int employeeId, String firstName, String lastName, Date birthDate,
                               String address, String phoneNumber, String status, String position,
                               String supervisor, CompensationDetails compensation,
                               GovernmentDetails governmentDetails) {
        super(employeeId, firstName, lastName, birthDate, address, phoneNumber,
                status, position, supervisor, compensation, governmentDetails);
    }

    @Override
    protected double getBenefits() {
        return 0;
    }
}