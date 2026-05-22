package payroll;

public class FullTimeEmployee extends Employee {

    private double allowances;

    public FullTimeEmployee(String employeeId, String name, String department, String position, double basicSalary, double allowances) {
        super(employeeId, name, department, position, basicSalary);
        this.allowances = allowances;
    }

    @Override
    public double computeGrossPay() {
        double overtimePay = computeOvertimePay();
        return basicSalary + allowances + overtimePay;
    }

    @Override
    public String getEmployeeType() { return "Full-Time Employee"; }

    public double getAllowances() { return allowances; }
}