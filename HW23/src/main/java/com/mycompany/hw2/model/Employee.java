package com.mycompany.hw2.model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Employee {

    public Employee() {
        this.employeeMap = new HashMap<>();
    }

    public Employee(String filePath) {
        this.employeeMap = new HashMap<>();
        this.filePath = filePath;
        loadEmployeesFromCSV(filePath);
    }

    private Map<Integer, EmployeeData> employeeMap = new HashMap<>();
    private String filePath;

    public void loadEmployeesFromCSV(String filePath) {
        this.filePath = filePath;
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] tokens;
            reader.readNext();

            while ((tokens = reader.readNext()) != null) {
                if (tokens.length < 19) {
                    System.out.println("Skipping line (too few columns): " + Arrays.toString(tokens));
                    continue;
                }

                try {
                    EmployeeData emp = CSVHandler.parseEmployeeRow(tokens);
                    if (emp != null) {
                        employeeMap.put(emp.getEmployeeId(), emp);
                        System.out.println("Loaded employee ID: " + emp.getEmployeeId());
                    }
                } catch (Exception ex) {
                    System.out.println("Skipping line due to parsing error: " + ex.getMessage());
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    public EmployeeData getEmployeeById(int id) {
        return employeeMap.get(id);
    }

    public EmployeeData findByName(String fullName) {
        for (EmployeeData emp : employeeMap.values()) {
            if (emp.getFullName().equalsIgnoreCase(fullName)) {
                return emp;
            }
        }
        return null;
    }

    public EmployeeData findById(int id) {
        return employeeMap.get(id);
    }

    public List<EmployeeData> getAllEmployees() {
        return new ArrayList<>(employeeMap.values());
    }

    public void addEmployee(EmployeeData newEmp) {
        if (employeeMap.containsKey(newEmp.getEmployeeId())) {
            System.out.println("Employee ID already exists: " + newEmp.getEmployeeId());
        } else {
            employeeMap.put(newEmp.getEmployeeId(), newEmp);
            System.out.println("Added new employee: " + newEmp.getEmployeeId());
        }
    }

    public void updateEmployee(int id, EmployeeData updatedEmp) {
        if (employeeMap.containsKey(id)) {
            employeeMap.put(id, updatedEmp);
            System.out.println("Updated employee ID: " + id);
            try {
                CSVHandler.saveEmployeeToCSV(filePath, getAllEmployees());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Employee ID not found: " + id);
        }
    }

    public void deleteEmployee(int id) {
        if (employeeMap.containsKey(id)) {
            employeeMap.remove(id);
            System.out.println("Deleted employee ID: " + id);
            try {
                CSVHandler.saveEmployeeToCSV(filePath, getAllEmployees());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Employee ID not found: " + id);
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        if (filePath != null && !filePath.trim().isEmpty()) {
            this.filePath = filePath;
        }
    }
}