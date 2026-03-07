package com.mycompany.hw2.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveService {

    private static final String FILE_PATH = "leaves.csv";

    public void fileLeave(LeaveManagement leave) throws IOException {
        File file = new File(FILE_PATH);
        boolean fileExists = file.exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

            if (!fileExists) {
                writer.write("LeaveID,EmployeeID,Type,StartDate,EndDate,Status");
                writer.newLine();
            }

            writer.write(leave.getLeaveId() + "," +
                    leave.getEmployeeId() + "," +
                    leave.getLeaveType() + "," +
                    leave.getStartDate() + "," +
                    leave.getEndDate() + "," +
                    leave.getStatus());
            writer.newLine();
        }
    }

    public List<LeaveManagement> loadAllLeaves() throws IOException {
        List<LeaveManagement> leaves = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return leaves;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {

                if (isFirstLine && line.startsWith("LeaveID")) {
                    isFirstLine = false;
                    continue;
                }
                isFirstLine = false;

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");


                if (parts.length < 6) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                LeaveManagement leave = new LeaveManagement(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim(),
                        parts[4].trim()
                );


                String statusStr = parts[5].trim().toUpperCase();
                if (statusStr.equals("APPROVED")) {
                    leave.approveLeave();
                } else if (statusStr.equals("REJECTED")) {
                    leave.rejectLeave();
                }

                leaves.add(leave);
            }
        }
        return leaves;
    }

    public void saveAllLeaves(List<LeaveManagement> leaves) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {

            writer.write("LeaveID,EmployeeID,Type,StartDate,EndDate,Status");
            writer.newLine();

            for (LeaveManagement leave : leaves) {
                writer.write(
                        leave.getLeaveId() + "," +
                        leave.getEmployeeId() + "," +
                        leave.getLeaveType() + "," +
                        leave.getStartDate() + "," +
                        leave.getEndDate() + "," +
                        leave.getStatus()
                );
                writer.newLine();
            }
        }
    }

    public void approveLeave(String leaveId) throws IOException {
        updateLeaveStatus(leaveId, LeaveManagement.LeaveStatus.APPROVED);
    }

    public void rejectLeave(String leaveId) throws IOException {
        updateLeaveStatus(leaveId, LeaveManagement.LeaveStatus.REJECTED);
    }

    private void updateLeaveStatus(String leaveId, LeaveManagement.LeaveStatus newStatus) throws IOException {

        List<LeaveManagement> leaves = loadAllLeaves();


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {

            writer.write("LeaveID,EmployeeID,Type,StartDate,EndDate,Status");
            writer.newLine();

            for (LeaveManagement leave : leaves) {

                if (leave.getLeaveId().equals(leaveId)) {
                    if (newStatus == LeaveManagement.LeaveStatus.APPROVED) {
                        leave.approveLeave();
                    } else {
                        leave.rejectLeave();
                    }
                }


                writer.write(leave.getLeaveId() + "," +
                        leave.getEmployeeId() + "," +
                        leave.getLeaveType() + "," +
                        leave.getStartDate() + "," +
                        leave.getEndDate() + "," +
                        leave.getStatus());
                writer.newLine();
            }
        }
    }
}