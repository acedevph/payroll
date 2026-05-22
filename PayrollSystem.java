package payroll;

import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PayrollSystem {
    private List<Employee> employees = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new PayrollSystem().run();
    }

    public void run() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     EMPLOYEE PAYROLL SYSTEM          ║");
        System.out.println("╚══════════════════════════════════════╝");

        // Seed demo data
        seedData();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = getIntInput("Enter choice: ");
            switch (choice) {
                case 1 -> addEmployee();
                case 2 -> enterHoursWorked();
                case 3 -> generatePayslip();
                case 4 -> viewAllEmployees();
                case 5 -> exportPayrollReport();
                case 6 -> removeEmployee();
                case 0 -> { running = false; System.out.println("Goodbye!"); }
                default -> System.out.println("[!] Invalid choice.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n┌─────────────────────────────┐");
        System.out.println("│          MAIN MENU          │");
        System.out.println("├─────────────────────────────┤");
        System.out.println("│ 1. Add Employee             │");
        System.out.println("│ 2. Enter Hours Worked       │");
        System.out.println("│ 3. Generate Payslip         │");
        System.out.println("│ 4. View All Employees       │");
        System.out.println("│ 5. Export Payroll Report    │");
        System.out.println("│ 6. Remove Employee          │");
        System.out.println("│ 0. Exit                     │");
        System.out.println("└─────────────────────────────┘");
    }

    private void addEmployee() {
        System.out.println("\n--- ADD EMPLOYEE ---");
        System.out.println("1. Full-Time  2. Part-Time");
        int type = getIntInput("Type: ");

        System.out.print("Employee ID  : "); String id = scanner.nextLine().trim();
        if (findEmployee(id) != null) { System.out.println("[!] ID already exists."); return; }
        System.out.print("Name         : "); String name = scanner.nextLine().trim();
        System.out.print("Department   : "); String dept = scanner.nextLine().trim();
        System.out.print("Position     : "); String pos  = scanner.nextLine().trim();

        if (type == 1) {
            double salary = getDoubleInput("Basic Salary  : ");
            double allow  = getDoubleInput("Allowances    : ");
            employees.add(new FullTimeEmployee(id, name, dept, pos, salary, allow));
        } else {
            double rate = getDoubleInput("Hourly Rate   : ");
            employees.add(new PartTimeEmployee(id, name, dept, pos, rate));
        }
        System.out.println("[✓] Employee added successfully.");
    }

    private void enterHoursWorked() {
        System.out.println("\n--- ENTER HOURS WORKED ---");
        System.out.print("Employee ID: "); String id = scanner.nextLine().trim();
        Employee emp = findEmployee(id);
        if (emp == null) { System.out.println("[!] Employee not found."); return; }
        double hours = getDoubleInput("Hours Worked This Period: ");
        emp.setHoursWorked(hours);
        System.out.println("[✓] Hours updated for " + emp.getName());
    }

    private void generatePayslip() {
        System.out.println("\n--- GENERATE PAYSLIP ---");
        System.out.print("Employee ID: "); String id = scanner.nextLine().trim();
        Employee emp = findEmployee(id);
        if (emp == null) { System.out.println("[!] Employee not found."); return; }
        String slip = buildPayslip(emp);
        System.out.println(slip);

        System.out.print("Save to file? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            String filename = "payslip_" + emp.getEmployeeId() + "_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
            writeToFile(filename, slip);
            System.out.println("[✓] Payslip saved to " + filename);
        }
    }

    private String buildPayslip(Employee emp) {
        double gross = emp.computeGrossPay();
        double overtimePay = emp.computeOvertimePay();
        double tax = emp.computeTax(gross);
        double sss = emp.computeSSS(gross);
        double philHealth = emp.computePhilHealth(gross);
        double pagIbig = emp.computePagIbig(gross);
        double netPay = emp.computeNetPay();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════╗\n");
        sb.append("║              PAYSLIP                        ║\n");
        sb.append("╠══════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Date       : %-30s║\n", date));
        sb.append(String.format("║  Emp ID     : %-30s║\n", emp.getEmployeeId()));
        sb.append(String.format("║  Name       : %-30s║\n", emp.getName()));
        sb.append(String.format("║  Department : %-30s║\n", emp.getDepartment()));
        sb.append(String.format("║  Position   : %-30s║\n", emp.getPosition()));
        sb.append(String.format("║  Type       : %-30s║\n", emp.getEmployeeType()));
        sb.append(String.format("║  Hours      : %-30.1f║\n", emp.getHoursWorked()));
        sb.append("╠══════════════════════════════════════════════╣\n");
        sb.append("║  EARNINGS                                   ║\n");
        sb.append("╠══════════════════════════════════════════════╣\n");
        if (emp instanceof FullTimeEmployee fte) {
            sb.append(String.format("║  Basic Salary   : %25.2f ║\n", fte.getBasicSalary()));
            sb.append(String.format("║  Allowances     : %25.2f ║\n", fte.getAllowances()));
        } else if (emp instanceof PartTimeEmployee pte) {
            double regPay = Math.min(emp.getHoursWorked(), 80) * pte.getHourlyRate();
            sb.append(String.format("║  Regular Pay    : %25.2f ║\n", regPay));
        }
        sb.append(String.format("║  Overtime Pay   : %25.2f ║\n", overtimePay));
        sb.append(String.format("║  GROSS PAY      : %25.2f ║\n", gross));
        sb.append("╠══════════════════════════════════════════════╣\n");
        sb.append("║  DEDUCTIONS                                 ║\n");
        sb.append("╠══════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Withholding Tax: %25.2f ║\n", tax));
        sb.append(String.format("║  SSS            : %25.2f ║\n", sss));
        sb.append(String.format("║  PhilHealth     : %25.2f ║\n", philHealth));
        sb.append(String.format("║  Pag-IBIG       : %25.2f ║\n", pagIbig));
        sb.append(String.format("║  Total Deduct.  : %25.2f ║\n", tax + sss + philHealth + pagIbig));
        sb.append("╠══════════════════════════════════════════════╣\n");
        sb.append(String.format("║  NET PAY        : %25.2f ║\n", netPay));
        sb.append("╚══════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    private void viewAllEmployees() {
        if (employees.isEmpty()) { System.out.println("[!] No employees found."); return; }
        System.out.println("\n--- ALL EMPLOYEES ---");
        System.out.printf("%-8s %-20s %-15s %-12s %-10s%n", "ID", "Name", "Department", "Type", "Basic/Rate");
        System.out.println("-".repeat(70));
        for (Employee e : employees) {
            String rate = (e instanceof PartTimeEmployee pte)
                    ? String.format("%.2f/hr", pte.getHourlyRate())
                    : String.format("%.2f", e.getBasicSalary());
            System.out.printf("%-8s %-20s %-15s %-12s %-10s%n",
                    e.getEmployeeId(), e.getName(), e.getDepartment(), e.getEmployeeType().split(" ")[0], rate);
        }
    }

    private void exportPayrollReport() {
        if (employees.isEmpty()) { System.out.println("[!] No employees to report."); return; }
        StringBuilder report = new StringBuilder();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        report.append("PAYROLL REPORT - ").append(date).append("\n");
        report.append("=".repeat(80)).append("\n");
        report.append(String.format("%-8s %-20s %-12s %-12s %-12s %-12s%n",
                "ID", "Name", "Type", "Gross Pay", "Deductions", "Net Pay"));
        report.append("-".repeat(80)).append("\n");

        double totalGross = 0, totalNet = 0;
        for (Employee e : employees) {
            double gross = e.computeGrossPay();
            double net = e.computeNetPay();
            double deductions = gross - net;
            totalGross += gross;
            totalNet += net;
            report.append(String.format("%-8s %-20s %-12s %-12.2f %-12.2f %-12.2f%n",
                    e.getEmployeeId(), e.getName(), e.getEmployeeType().split(" ")[0],
                    gross, deductions, net));
        }
        report.append("=".repeat(80)).append("\n");
        report.append(String.format("%-42s %-12.2f %-12.2f %-12.2f%n",
                "TOTALS (" + employees.size() + " employees)",
                totalGross, totalGross - totalNet, totalNet));

        String filename = "payroll_report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
        writeToFile(filename, report.toString());
        System.out.println(report);
        System.out.println("[✓] Report exported to " + filename);
    }

    private void removeEmployee() {
        System.out.print("Employee ID to remove: ");
        String id = scanner.nextLine().trim();
        Employee emp = findEmployee(id);
        if (emp == null) { System.out.println("[!] Employee not found."); return; }
        employees.remove(emp);
        System.out.println("[✓] " + emp.getName() + " removed.");
    }

    private Employee findEmployee(String id) {
        return employees.stream().filter(e -> e.getEmployeeId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    private void writeToFile(String filename, String content) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.print(content);
        } catch (IOException e) {
            System.out.println("[!] Error writing file: " + e.getMessage());
        }
    }

    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { int v = Integer.parseInt(scanner.nextLine().trim()); return v; }
            catch (NumberFormatException e) { System.out.println("[!] Enter a valid number."); }
        }
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { double v = Double.parseDouble(scanner.nextLine().trim()); return v; }
            catch (NumberFormatException e) { System.out.println("[!] Enter a valid number."); }
        }
    }

    private void seedData() {
        FullTimeEmployee e1 = new FullTimeEmployee("EMP001", "Ace Dev", "Engineering", "Software Engineer", 55000, 5000);
        e1.setHoursWorked(175);
        FullTimeEmployee e2 = new FullTimeEmployee("EMP002", "Kishuru Hachwa", "Developer", "Web Developer", 45000, 3000);
        e2.setHoursWorked(160);
        PartTimeEmployee e3 = new PartTimeEmployee("EMP003", "Zyke Kavenski", "HR", "HR Assistant", 180);
        e3.setHoursWorked(90);
        employees.add(e1); employees.add(e2); employees.add(e3);
    }
}