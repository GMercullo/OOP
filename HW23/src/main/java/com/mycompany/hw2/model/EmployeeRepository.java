package com.mycompany.hw2.model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class EmployeeRepository {

    private Map<Integer, EmployeeData> employeeMap = new HashMap<>();
    private String filePath;

    public EmployeeRepository() {
        this.employeeMap = new HashMap<>();
    }

    public EmployeeRepository(String filePath) {
        this.employeeMap = new HashMap<>();
        this.filePath = filePath;
        loadEmployeesFromCSV(filePath);
    }

    public void loadEmployeesFromCSV(String filePath) {
        this.filePath = filePath;
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] tokens;
            reader.readNext();

            while ((tokens = reader.readNext()) != null) {
                if (tokens.length < 19) {
                    continue;
                }

                try {
                    EmployeeData emp = CSVHandler.parseEmployeeRow(tokens);
                    if (emp != null) {
                        employeeMap.put(emp.getEmployeeId(), emp);
                    }
                } catch (Exception ex) {
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
        if (!employeeMap.containsKey(newEmp.getEmployeeId())) {
            employeeMap.put(newEmp.getEmployeeId(), newEmp);
        }
    }

    public void updateEmployee(int id, EmployeeData updatedEmp) {
        if (employeeMap.containsKey(id)) {
            employeeMap.put(id, updatedEmp);
            try {
                CSVHandler.saveEmployeeToCSV(filePath, getAllEmployees());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteEmployee(int id) {
        if (employeeMap.containsKey(id)) {
            employeeMap.remove(id);
            try {
                CSVHandler.saveEmployeeToCSV(filePath, getAllEmployees());
            } catch (IOException e) {
                e.printStackTrace();
            }
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
