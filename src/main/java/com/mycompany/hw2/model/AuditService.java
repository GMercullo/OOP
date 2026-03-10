package com.mycompany.hw2.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class AuditService {

    private static final String FILE = "audit_log.csv";

    public static void log(String role, String action, String details) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE, true))) {

            String entry = LocalDateTime.now() + "," + role + "," + action + "," + details;
            bw.write(entry);
            bw.newLine();

        } catch (IOException e) {
            System.err.println("Audit log error: " + e.getMessage());
        }
    }

}
