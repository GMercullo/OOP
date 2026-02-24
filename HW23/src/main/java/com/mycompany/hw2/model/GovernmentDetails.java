/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hw2.model;

/**
 * Represents an employee's government identification details, such as SSS, PhilHealth,
 * Pag-IBIG, and TIN numbers.
 * 
 * Author: gmmercullo
 */
public class GovernmentDetails {
    private String sssNumber;
    private String philHealthNumber;
    private String tinNumber;
    private String pagIbigNumber;

    public GovernmentDetails (String sssNumber, String philHealthNumber, String tinNumber, String pagIbigNumber) {
        if (sssNumber == null || philHealthNumber == null || tinNumber == null || pagIbigNumber == null) {
            throw new IllegalArgumentException("Government ID vlaues must not be null.");
        }
        this.sssNumber = sssNumber;
        this.philHealthNumber = philHealthNumber;
        this.tinNumber = tinNumber;
        this.pagIbigNumber = pagIbigNumber;
    }

    public String getSssNumber() { return sssNumber; }
    public String getPhilHealthNumber() { return philHealthNumber; }
    public String getTinNumber() { return tinNumber; }
    public String getPagIbigNumber() { return pagIbigNumber; }

    public void setSssNumber (String sssNumber) {
        if (sssNumber != null && !sssNumber.trim().isEmpty()) {
            this.sssNumber = sssNumber;
        }
    }

    public void setPhilHealthNumber (String philHealthNumber) {
        if (philHealthNumber != null && !philHealthNumber.trim().isEmpty()) {
            this.philHealthNumber = philHealthNumber;
        }
    }

    public void setTinNumber (String tinNumber) {
        if (tinNumber != null && !tinNumber.trim().isEmpty()) {
            this.tinNumber = tinNumber;
        }
    }

    public void setPagIbigNumber (String pagIbigNumber) {
        if (pagIbigNumber != null && !pagIbigNumber.trim().isEmpty()) {
            this.pagIbigNumber = pagIbigNumber;
        }
    }

}
