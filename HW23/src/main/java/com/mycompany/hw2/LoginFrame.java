package com.mycompany.hw2;

import com.opencsv.CSVReader;
import javax.swing.*;
import java.awt.*;
import java.io.*;


public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    private String userRole = null;

    private static final String LOGIN_CSV = "src/users.csv";

    public LoginFrame() {
        setTitle("MotorPH Login");
        setSize(500, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 5, 5));

        add(new JLabel("Username:"));
        usernameField = new JTextField(); add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField(); add(passwordField);

        JButton loginButton = new JButton("Login"); add(loginButton);
        messageLabel = new JLabel("", SwingConstants.CENTER); add(messageLabel);

        loginButton.addActionListener(e -> authenticateUser());
    }

    private void authenticateUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try (
                BufferedReader reader = new BufferedReader(new FileReader(LOGIN_CSV));
                CSVReader csvReader = new CSVReader(reader)
        ) {
            String[] line;
            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {

                if (line.length >= 4 &&
                        line[0].trim().equalsIgnoreCase(username) &&
                        line[1].trim().equals(password)) {


                    String correctFullName = line[2].trim();
                    userRole = line[3].trim().toUpperCase();

                    System.out.println("Logged in as role: " + userRole);
                    System.out.println("Using CSV Full Name: " + correctFullName);

                    dispose();


                    SwingUtilities.invokeLater(() -> new HW2(userRole, correctFullName).setVisible(true));
                    return;
                }
            }

            messageLabel.setText("Invalid username or password.");

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            messageLabel.setText("Login Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}