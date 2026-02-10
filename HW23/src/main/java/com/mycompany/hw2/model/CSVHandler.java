package com.mycompany.hw2.model;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

public final class CSVHandler {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    public static EmployeeData parseEmployeeRow(String[] tokens) {
        if (tokens.length < 19) {
            System.out.println("Skipping malformed row: " + String.join(",", tokens));
            return null;
        }

        // H2W, Log In frame, Attendance, Compensation details, CSV handler, deductions,
        // employee, employeeData, Gov. details, grosswage


        int employeeId = parseInt(tokens[0]);
        String lastName = tokens[1].trim();
        String firstName = tokens[2].trim();
        Date birthDate = parseDate(tokens[3]);
        String address = tokens[4].trim();
        String phone = tokens[5].trim();

        String position = tokens[6].trim();
        String status = tokens[7].trim();
        String supervisor = tokens[8].trim();

        String sssNumber = tokens[9].trim();
        String philHealthNumber = tokens[10].trim();
        String tinNumber = tokens[11].trim();
        String pagIbigNumber = tokens[12].trim();

        double basicSalary = parseDouble(tokens[13]);
        double riceSubsidy = parseDouble(tokens[14]);
        double phoneAllowance = parseDouble(tokens[15]);
        double clothingAllowance = parseDouble(tokens[16]);
        double grossSemiMonthlyRate = parseDouble(tokens[17]);
        double hourlyRate = parseDouble(tokens[18]);

        CompensationDetails compensation = new CompensationDetails(
                basicSalary, riceSubsidy, phoneAllowance, clothingAllowance,
                grossSemiMonthlyRate, hourlyRate
        );

        GovernmentDetails govDetails = new GovernmentDetails(
                sssNumber, philHealthNumber, tinNumber, pagIbigNumber
        );

        return new RegularEmployee(
                employeeId, firstName, lastName, birthDate, address, phone,
                status, position, supervisor, compensation, govDetails
        );
    }

    public static void saveEmployeeToCSV(String filePath, List<EmployeeData> allEmployees) throws IOException {
        File file = new File(filePath);

        try (CSVWriter writer = new CSVWriter(
                new FileWriter(file),
                CSVWriter.DEFAULT_SEPARATOR,
                '"',
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {

            writer.writeNext(getCSVHeader());

            for (EmployeeData emp : allEmployees) {
                String birthDateStr = emp.getBirthDate() != null
                        ? DATE_FORMAT.format(emp.getBirthDate())
                        : "";

                writer.writeNext(new String[]{
                        String.valueOf(emp.getEmployeeId()),
                        emp.getLastName(),
                        emp.getFirstName(),
                        birthDateStr,
                        emp.getAddress(),
                        emp.getPhoneNumber(),
                        emp.getPosition(),
                        emp.getStatus(),
                        emp.getSupervisor(),
                        emp.getGovernmentDetails().getSssNumber(),
                        emp.getGovernmentDetails().getPhilHealthNumber(),
                        emp.getGovernmentDetails().getTinNumber(),
                        emp.getGovernmentDetails().getPagIbigNumber(),
                        String.valueOf(emp.getCompensation().getBasicSalary()),
                        String.valueOf(emp.getCompensation().getRiceSubsidy()),
                        String.valueOf(emp.getCompensation().getPhoneAllowance()),
                        String.valueOf(emp.getCompensation().getClothingAllowance()),
                        String.valueOf(emp.getCompensation().getGrossSemiMonthlyRate()),
                        String.format("%.2f", emp.getCompensation().getHourlyRate())
                });
            }
        }
    }


    public static void appendEmployeeToCSV(String filePath, EmployeeData emp) throws IOException {
        File file = new File(filePath);
        boolean fileExists = file.exists();

        try (CSVWriter writer = new CSVWriter(
                new FileWriter(file, true),
                CSVWriter.DEFAULT_SEPARATOR,
                '"',
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {

            if (!fileExists || file.length() == 0) {
                writer.writeNext(getCSVHeader());
            }

            String birthDateStr = emp.getBirthDate() != null
                    ? DATE_FORMAT.format(emp.getBirthDate())
                    : "";

            writer.writeNext(new String[]{
                    String.valueOf(emp.getEmployeeId()),
                    emp.getLastName(),
                    emp.getFirstName(),
                    birthDateStr,
                    emp.getAddress(),
                    emp.getPhoneNumber(),
                    emp.getPosition(),
                    emp.getStatus(),
                    emp.getSupervisor(),
                    emp.getGovernmentDetails().getSssNumber(),
                    emp.getGovernmentDetails().getPhilHealthNumber(),
                    emp.getGovernmentDetails().getTinNumber(),
                    emp.getGovernmentDetails().getPagIbigNumber(),
                    String.valueOf(emp.getCompensation().getBasicSalary()),
                    String.valueOf(emp.getCompensation().getRiceSubsidy()),
                    String.valueOf(emp.getCompensation().getPhoneAllowance()),
                    String.valueOf(emp.getCompensation().getClothingAllowance()),
                    String.valueOf(emp.getCompensation().getGrossSemiMonthlyRate()),
                    String.format("%.2f", emp.getCompensation().getHourlyRate())
            });
        }
    }

    private static String[] getCSVHeader() {
        return new String[]{
                "EmployeeID", "LastName", "FirstName", "BirthDate", "Address", "Phone",
                "Position", "Status", "Supervisor",
                "SSS", "PhilHealth", "TIN", "PagIbig",
                "BasicSalary", "RiceSubsidy", "PhoneAllowance", "ClothingAllowance",
                "GrossSemiMonthly", "HourlyRate"
        };
    }

    public static List<EmployeeData> loadAllEmployees(String filePath) throws IOException {
        List<EmployeeData> employees = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            boolean isHeader = true;

            while ((nextLine = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                EmployeeData emp = parseEmployeeRow(nextLine);
                if (emp != null) {
                    employees.add(emp);
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("CSV read error", e);
        }

        return employees;
    }

    public static double parseDouble(String input) {
        try {
            return Double.parseDouble(input.replace(",", "").trim());
        } catch (NumberFormatException e) {
            System.out.println("Failed to parse double from: '" + input + "', defaulting to 0.0");
            return 0.0;
        }
    }

    public static int parseInt(String str) {
        if (isEmpty(str)) {
            return logParseError("int", str, 0);
        }
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return logParseError("int", str, 0);
        }
    }

    public static Date parseDate(String dateStr) {
        if (isEmpty(dateStr)) {
            System.out.println("Empty or null date string, returning null");
            return null;
        }
        try {
            return DATE_FORMAT.parse(dateStr.trim());
        } catch (ParseException e) {
            System.out.println("Failed to parse date from: '" + dateStr + "', returning null");
            return null;
        }
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private static <T> T logParseError(String type, String input, T defaultValue) {
        System.out.println("Failed to parse " + type + " from: '" + input + "', defaulting to " + defaultValue);
        return defaultValue;
    }
}
