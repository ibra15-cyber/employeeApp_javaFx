package com.ibra.employeeapplication.backend.service;

import com.ibra.employeeapplication.backend.entity.Employee;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SalaryManager<T> {
    private EmployeeDB<T> database;

    public SalaryManager(EmployeeDB<T> database) {
        this.database = database;
    }

    /**
     * Give salary raises to employees with performance ratings meeting or exceeding the threshold
     * @param minimumRating The minimum rating required for a raise
     * @param percentageRaise The percentage raise to apply (e.g., 10 for 10%)
     * @return Number of employees who received raises
     */
    public int giveSalaryRaiseByPerformance(double minimumRating, double percentageRaise) {
        if (percentageRaise <= 0) {
            throw new IllegalArgumentException("Percentage raise must be positive");
        }

        List<Employee<T>> eligibleEmployees = database.getAllEmployees().stream()
                .filter(emp -> emp.getPerformanceRating() >= minimumRating && emp.isActive())
                .collect(Collectors.toList());

        eligibleEmployees.forEach(emp -> {
            double currentSalary = emp.getSalary();
            double newSalary = currentSalary * (1 + percentageRaise / 100);
            emp.setSalary(newSalary);
        });

        return eligibleEmployees.size();
    }

    /**
     * Give years of experience-based raises to employees
     * @param yearsThreshold Minimum years of experience required
     * @param percentageRaise The percentage raise to apply
     * @return Number of employees who received raises
     */
    public int giveSalaryRaiseByExperience(int yearsThreshold, double percentageRaise) {
        if (percentageRaise <= 0) {
            throw new IllegalArgumentException("Percentage raise must be positive");
        }

        List<Employee<T>> eligibleEmployees = database.getAllEmployees().stream()
                .filter(emp -> emp.getYearsOfExperience() >= yearsThreshold && emp.isActive())
                .collect(Collectors.toList());

        eligibleEmployees.forEach(emp -> {
            double currentSalary = emp.getSalary();
            double newSalary = currentSalary * (1 + percentageRaise / 100);
            emp.setSalary(newSalary);
        });

        return eligibleEmployees.size();
    }

    /**
     * Give raises to all employees in a specific department
     * @param department The department to target
     * @param percentageRaise The percentage raise to apply
     * @return Number of employees who received raises
     */
    public int giveSalaryRaiseByDepartment(String department, double percentageRaise) {
        if (percentageRaise <= 0) {
            throw new IllegalArgumentException("Percentage raise must be positive");
        }

        List<Employee<T>> eligibleEmployees = database.getAllEmployees().stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(department) && emp.isActive())
                .collect(Collectors.toList());

        eligibleEmployees.forEach(emp -> {
            double currentSalary = emp.getSalary();
            double newSalary = currentSalary * (1 + percentageRaise / 100);
            emp.setSalary(newSalary);
        });

        return eligibleEmployees.size();
    }

    /**
     * Get the top N highest-paid employees
     * @param n Number of employees to return
     * @return List of the top N highest-paid employees
     */
    public List<Employee<T>> getTopPaidEmployees(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Number of employees must be positive");
        }

        return database.getAllEmployees().stream()
                .sorted()
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Calculate the average salary across all employees
     * @return The average salary
     */
    public double calculateAverageSalary() {
        return database.getAllEmployees().stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    /**
     * Calculate the average salary for employees in a specific department
     * @param department The department to calculate average for
     * @return The average salary in the specified department
     */
    public double calculateAverageSalaryByDepartment(String department) {
        return database.getAllEmployees().stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    /**
     * Calculate the average salary for each department
     * @return Map of department names to average salaries
     */
    public Map<String, Double> calculateAverageSalaryPerDepartment() {
        return database.getAllEmployees().stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.averagingDouble(Employee::getSalary)
                ));
    }

    /**
     * Calculate the total salary cost for the company
     * @return The total salary cost
     */
    public double calculateTotalSalaryCost() {
        return database.getAllEmployees().stream()
                .filter(Employee::isActive)
                .mapToDouble(Employee::getSalary)
                .sum();
    }

    /**
     * Calculate the total salary cost by department
     * @return Map of department names to total salary costs
     */
    public Map<String, Double> calculateTotalSalaryCostPerDepartment() {
        return database.getAllEmployees().stream()
                .filter(Employee::isActive)
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.summingDouble(Employee::getSalary)
                ));
    }

    /**
     * Find the salary difference between highest and lowest paid employees
     * @return The salary gap
     */
    public double calculateSalaryGap() {
        DoubleSummaryStatistics stats = database.getAllEmployees().stream()
                .mapToDouble(Employee::getSalary)
                .summaryStatistics();

        return stats.getMax() - stats.getMin();
    }

    /**
     * Check if any employees in a department earn above a threshold
     * @param department The department to check
     * @param salaryThreshold The salary threshold
     * @return true if any employees earn above the threshold
     */
    public boolean anyEmployeesAboveSalary(String department, double salaryThreshold) {
        return database.getAllEmployees().stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                .anyMatch(emp -> emp.getSalary() > salaryThreshold);
    }

    /**
     * Display formatted salary report for employees
     * @param employees List of employees to include in report
     */
    public void displaySalaryReport(List<Employee<T>> employees) {
        if (employees.isEmpty()) {
            System.out.println("No employees to display in salary report.");
            return;
        }

        System.out.println("\n============= SALARY REPORT =============");
        System.out.printf("%-20s | %-15s | %-10s | %s\n", "NAME", "DEPARTMENT", "SALARY", "RATING");
        System.out.println("------------------------------------------");

        employees.forEach(emp -> {
            System.out.printf("%-20s | %-15s | $%,9.2f | %.1f\n",
                    emp.getName(),
                    emp.getDepartment(),
                    emp.getSalary(),
                    emp.getPerformanceRating());
        });

        System.out.println("=========================================");
    }
}