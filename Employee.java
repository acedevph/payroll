package payroll;

public abstract class Employee {
    protected String employeeId;
    protected String name;
    protected String department;
    protected String position;
    protected double basicSalary;
    protected double hoursWorked;
    protected double regularHours;

    public Employee(String employeeId, String name, String department, String position, double basicSalary) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.position = position;
        this.basicSalary = basicSalary;
        this.regularHours = 160.0; // standard monthly hours
    }

    public abstract double computeGrossPay();
    public abstract String getEmployeeType();

    public double computeOvertimePay() {
        double overtimeHours = Math.max(0, hoursWorked - regularHours);
        double hourlyRate = basicSalary / regularHours;
        return overtimeHours * hourlyRate * 1.25; // 25% overtime premium
    }

    public double computeTax(double grossPay) {
        // Philippine BIR simplified tax brackets
        if (grossPay <= 20833) return 0;
        else if (grossPay <= 33333) return (grossPay - 20833) * 0.20;
        else if (grossPay <= 66667) return 2500 + (grossPay - 33333) * 0.25;
        else if (grossPay <= 166667) return 10833 + (grossPay - 66667) * 0.30;
        else if (grossPay <= 666667) return 40833.33 + (grossPay - 166667) * 0.32;
        else return 200833.33 + (grossPay - 666667) * 0.35;
    }

    public double computeSSS(double grossPay) {
        // Simplified SSS contribution
        return Math.min(grossPay * 0.045, 900);
    }

    public double computePhilHealth(double grossPay) {
        return grossPay * 0.025;
    }

    public double computePagIbig(double grossPay) {
        return Math.min(grossPay * 0.02, 200);
    }

    public double computeNetPay() {
        double gross = computeGrossPay();
        double tax = computeTax(gross);
        double sss = computeSSS(gross);
        double philHealth = computePhilHealth(gross);
        double pagIbig = computePagIbig(gross);
        return gross - tax - sss - philHealth - pagIbig;
    }

    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }
    public double getBasicSalary() { return basicSalary; }
    public double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(double hoursWorked) { this.hoursWorked = hoursWorked; }
    public void setBasicSalary(double salary) { this.basicSalary = salary; }
}