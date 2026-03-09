package com.mycompany.hw2;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.opencsv.CSVReader;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    private static final String LOGIN_CSV = "src/users.csv";

    public LoginFrame() {

        setTitle("MotorPH Login");
        setSize(500, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 5, 5));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JButton loginButton = new JButton("Login");
        add(loginButton);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        add(messageLabel);

        loginButton.addActionListener(e -> {

            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            String role = authenticateUser(username, password);

            if (role != null) {

                if (role.equalsIgnoreCase("ADMIN") ||

                        role.equalsIgnoreCase("HR") ||
                        role.equalsIgnoreCase("FINANCE") ||
                        role.equalsIgnoreCase("IT")) {

                    int choice = JOptionPane.showOptionDialog(
                            this,
                            "Logging in as",
                            "Access Mode",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new String[]{"Admin", "Employee"},
                            "Administrative Role"
                    );

                    if (choice == 0) {
                        new HW2(role, username).setVisible(true);
                    } else {
                        new HW2("EMPLOYEE", username).setVisible(true);
                    }

                } else {

                    new HW2(role, username).setVisible(true);
                }

                dispose();

            } else {

                messageLabel.setText("Invalid username or password.");
            }
        });
    }

    /*
    ISSUE: Passwords are currently stored and validated as plain text from users.csv.
    This approach is not secure and would be unsafe in a production system.
    Please remove comment if resolved.®
    - GM Mercullo (03-05-26)
     */


    private String authenticateUser(String username, String password) {

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

                    return line[3].trim().toUpperCase();
                }
            }

        } catch (Exception e) {

            System.err.println("Login error: " + e.getMessage());
        }

        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}