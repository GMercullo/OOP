import java.util.*;
import java.io.*;

public class EmployeeCSV {
    public static ArrayList<Employee> readEmployees(String filename) {
        ArrayList<Employee> employees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] e = line.split(",");
                if (e.length >= 7) employees.add(new Employee(e[0], e[1], e[2], e[3], e[4], e[5], e[6]));
            }
        } catch (IOException ex) { ex.printStackTrace(); }
        return employees;
    }
    public static void writeEmployees(String filename, ArrayList<Employee> employees) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, false))) {
            for (Employee emp : employees) {
                bw.write(String.join(",", emp.toArray()));
                bw.newLine();
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }
    public static void appendEmployee(String filename, Employee emp) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(String.join(",", emp.toArray()));
            bw.newLine();
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}