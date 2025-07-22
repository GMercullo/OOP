import java.util.*;
public class EmployeeManager {
    private ArrayList<Employee> employees = new ArrayList<>();
    public void add(Employee e) { employees.add(e); }
    public void update(int idx, Employee e) { employees.set(idx, e); }
    public void delete(int idx) { employees.remove(idx); }
    public ArrayList<Employee> getAll() { return employees; }
    public Employee get(int idx) { return employees.get(idx); }
    public int size() { return employees.size(); }
    public int sumEmpNumbers() {
        int sum = 0;
        for (Employee e : employees) sum += Integer.parseInt(e.getEmpNumber());
        return sum;
    }
    public int maxEmpNumber() {
        int max = 0;
        for (Employee e : employees) {
            int num = Integer.parseInt(e.getEmpNumber());
            if (num > max) max = num;
        }
        return max;
    }
}