public class Employee {
    private String empNumber, lastName, firstName, sssNumber, philHealthNumber, tin, pagIbigNumber;
    public Employee(String en, String ln, String fn, String sss, String ph, String tin, String pag) {
        empNumber = en; lastName = ln; firstName = fn; sssNumber = sss;
        philHealthNumber = ph; this.tin = tin; pagIbigNumber = pag;
    }
    public String getEmpNumber() { return empNumber; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getSssNumber() { return sssNumber; }
    public String getPhilHealthNumber() { return philHealthNumber; }
    public String getTin() { return tin; }
    public String getPagIbigNumber() { return pagIbigNumber; }
    public String[] toArray() { return new String[]{empNumber, lastName, firstName, sssNumber, philHealthNumber, tin, pagIbigNumber}; }
}