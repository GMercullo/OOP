package com.mycompany.hw2.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;

public class Attendance {
    // Default constructor
    public Attendance() {
        // No state initialization required
    }

    private static final String ATTENDANCE_CSV_FILE = "src/MotorPH Employee Data - Attendance Record.csv";
    private static final Logger logger = Logger.getLogger(Attendance.class.getName());

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
    private static final double MAX_REGULAR_HOURS = 8.0;
    private static final double LUNCH_BREAK_HOURS = 1.0;

    // Returns total regular hours worked by the employee within a date range
    public double getTotalRegularHours(int employeeId, LocalDate start, LocalDate end) {
        HoursWorked hours = calculateHours(employeeId, start, end);
        return hours.getRegularHours();
    }

    // Returns total overtime hours worked by the employee within a date range
    public double getTotalOvertimeHours(int employeeId, LocalDate start, LocalDate end) {
        HoursWorked hours = calculateHours(employeeId, start, end);
        return hours.getOvertimeHours();
    }

    // Returns the months (MM-yyyy) where the employee has attendance records
    public List<String> getAvailableMonths(int employeeId) {
        Set<String> uniqueMonths = new LinkedHashSet<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM-yyyy");

        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_CSV_FILE))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 6) {
                    logger.warning("Skipping invalid record: " + line);
                    continue;
                }

                int id = CSVHandler.parseInt(parts[0]);
                LocalDate date = parseDate(parts[3].trim());

                if (id != employeeId || date == null) continue;

                uniqueMonths.add(date.format(monthFormatter));
            }
        } catch (IOException e) {
            logger.warning("Error reading attendance file: " + e.getMessage());
        }

        return new ArrayList<>(uniqueMonths);
    }

    /**
     * Checks if attendance data exists for the given employee and month (format: MM-yyyy).
     */
    public boolean hasAttendanceForMonth(int employeeId, String targetMonth) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM-yyyy");

        try {
            YearMonth targetYearMonth = YearMonth.parse(targetMonth, inputFormatter);

            try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_CSV_FILE))) {
                String line;
                boolean isFirstLine = true;

                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    String[] parts = line.split(",");
                    if (parts.length < 6) continue;

                    int id = CSVHandler.parseInt(parts[0]);
                    LocalDate date = parseDate(parts[3].trim());

                    if (id != employeeId || date == null) continue;

                    if (YearMonth.from(date).equals(targetYearMonth)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.warning("Error checking attendance for month: " + e.getMessage());
        }

        return false;
    }

    // Computes the total regular and overtime hours between two dates
    public HoursWorked calculateHours(int employeeId, LocalDate start, LocalDate end) {
        double totalRegular = 0;
        double totalOvertime = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_CSV_FILE))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 6) continue;

                int id = CSVHandler.parseInt(parts[0]);
                LocalDate date = parseDate(parts[3].trim());
                LocalTime login = parseTime(parts[4].trim());
                LocalTime logout = parseTime(parts[5].trim());

                if (id != employeeId || date == null || login == null || logout == null) continue;
                if (date.isBefore(start) || date.isAfter(end)) continue;

                double dailyHours = ChronoUnit.MINUTES.between(login, logout) / 60.0;
                dailyHours -= LUNCH_BREAK_HOURS;

                if (dailyHours <= 0) continue;

                dailyHours = Math.round(dailyHours * 10.0) / 10.0;

                if (dailyHours <= MAX_REGULAR_HOURS) {
                    totalRegular += dailyHours;
                } else {
                    totalRegular += MAX_REGULAR_HOURS;
                    totalOvertime += (dailyHours - MAX_REGULAR_HOURS);
                }
            }
        } catch (IOException e) {
            logger.warning("Error reading attendance CSV file: " + e.getMessage());
        }

        return new HoursWorked(totalRegular, totalOvertime);
    }

    // Parses a date string
    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            logger.warning("Invalid date: " + dateStr);
            return null;
        }
    }

    // Parses a time string
    private LocalTime parseTime(String timeStr) {
        try {
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        } catch (Exception e) {
            logger.warning("Invalid time: " + timeStr);
            return null;
        }
    }

    // Container for regular and overtime hours
    public static class HoursWorked {
        private final double regularHours;
        private final double overtimeHours;

        public HoursWorked(double regularHours, double overtimeHours) {
            this.regularHours = regularHours;
            this.overtimeHours = overtimeHours;
        }

        public double getRegularHours() {
            return regularHours;
        }

        public double getOvertimeHours() {
            return overtimeHours;
        }
    }
}