public class SalaryCalculator {
    public static double calculate(Employee emp, String month, int daysWorked) {
        double[] monthlyRates = {15000, 16000, 17000, 18000, 19000, 20000, 21000, 22000, 23000, 24000, 25000, 26000};
        int monthIdx = getMonthIndex(month);
        if (monthIdx < 0) monthIdx = 0;
        double base = monthlyRates[monthIdx];
        try {
            if (daysWorked == 0) throw new ArithmeticException("Division by zero: days worked is zero!");
            return base * daysWorked / 22;
        } catch (ArithmeticException ex) {
            return 0.0;
        }
    }
    public static int getMonthIndex(String month) {
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        for (int i=0; i<months.length; i++) if (months[i].equalsIgnoreCase(month)) return i;
        return -1;
    }
}