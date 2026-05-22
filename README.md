# Employee Payroll System
**Package:** `payroll` | **Main Class:** `PayrollSystem`

---

## Description
A payroll management system that handles employee records, computes salaries with overtime pay, applies government-mandated deductions, and generates detailed payslips.

---

## Features
- Add full-time and part-time employees
- Enter hours worked per pay period
- Compute gross pay with overtime (25% premium for extra hours)
- Apply deductions: Withholding Tax, SSS, PhilHealth, Pag-IBIG
- Generate and save payslips as `.txt` files
- Export full payroll report to a file
- View all employees
- Remove employees from the system

---

## File Structure
```
payroll/
├── Employee.java           ← Abstract base class
├── FullTimeEmployee.java   ← Extends Employee (salary + allowances)
├── PartTimeEmployee.java   ← Extends Employee (hourly rate)
└── PayrollSystem.java      ← Main class
```

---
## Deductions Formula
| Deduction | Rate |
|-----------|------|
| Withholding Tax | Based on BIR tax brackets |
| SSS | 4.5% of gross (max PHP 900) |
| PhilHealth | 2.5% of gross |
| Pag-IBIG | 2% of gross (max PHP 200) |

Overtime pay = extra hours × (basic rate × 1.25)

---

## How to Compile & Run

**Step 1 — Create output folder**
```bash
mkdir -p out/payroll
```

**Step 2 — Compile**
```bash
javac -d out/payroll payroll/*.java
```

**Step 3 — Run**
```bash
java -cp out/payroll payroll.PayrollSystem
```

---

## Generated Files
| File | Description |
|------|-------------|
| `payslip_EMPxxx_yyyyMMdd.txt` | Individual payslip saved per employee |
| `payroll_report_yyyyMMdd.txt` | Full payroll summary of all employees |

---

## Common Errors
| Error | Fix |
|-------|-----|
| `package payroll does not match` | Make sure files are inside a folder named exactly `payroll` |
| `cannot find symbol` | Compile all `.java` files together, not one by one |
| `javac: command not found` | Run `sudo apt install default-jdk -y` |
