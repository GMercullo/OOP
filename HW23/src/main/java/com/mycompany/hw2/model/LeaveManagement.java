package com.mycompany.hw2.model;

public class LeaveManagement {

    public enum LeaveStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    private String leaveId;
    private String employeeId;
    private String leaveType;
    private String startDate;
    private String endDate;
    private LeaveStatus status;

    public LeaveManagement(String leaveId, String employeeId, String leaveType, String startDate, String endDate) {
        this.leaveId = leaveId;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = LeaveStatus.PENDING;
    }

    public void approveLeave() {
        this.status = LeaveStatus.APPROVED;
    }

    public void rejectLeave() {
        this.status = LeaveStatus.REJECTED;
    }

    public String getLeaveId() {
        return leaveId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setLeaveType(String leaveType) {
        if (leaveType == null || leaveType.isBlank()) {
            throw new IllegalArgumentException("Leave type cannot be empty.");
        }
        this.leaveType = leaveType;
    }

    public void setStartDate(String startDate) {
        if (startDate == null || startDate.isBlank()) {
            throw new IllegalArgumentException("Start date cannot be empty.");
        }
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        if (endDate == null || endDate.isBlank()) {
            throw new IllegalArgumentException("End date cannot be empty.");
        }
        this.endDate = endDate;
    }
}