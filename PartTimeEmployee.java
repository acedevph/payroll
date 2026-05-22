package payroll;

public class PartTimeEmployee extends Employee {

    private double hourlyRate;

    public PartTimeEmployee(String employeeId, String name, String department, String position, double hourlyRate) {
        super(employeeId, name, department, position, 0);
        this.hourlyRate = hourlyRate;
        this.regularHours = 80.0; // part-time standard
    }

    @Override
    public double computeGrossPay() {
        double regularPay = Math.min(hoursWorked, regularHours) * hourlyRate;
        double overtimePay = computeOvertimePay();
        return regularPay + overtimePay;
    }

    @Override
    public double computeOvertimePay() {
        double overtimeHours = Math.max(0, hoursWorked - regularHours);
        return overtimeHours * hourlyRate * 1.25;
    }

    @Override
    public String getEmployeeType() { return "Part-Time Employee"; }

    public double getHourlyRate() { return hourlyRate; }
}