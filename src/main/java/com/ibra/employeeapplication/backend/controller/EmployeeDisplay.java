package com.ibra.employeeapplication.backend.controller;

import com.ibra.employeeapplication.backend.entity.Employee;
import com.ibra.employeeapplication.backend.service.EmployeeDB;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeDisplay<T> {
    private EmployeeDB<T> database;

    public EmployeeDisplay(EmployeeDB<T> database) {
        this.database = database;
    }

    /**
     * Display all employees using for-each loop with formatted output
     */
    public void displayAllEmployeesForEach() {
        List<Employee<T>> employees = database.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees to display.");
            return;
        }

        System.out.println("\n============= Employee List =============");
        System.out.printf("%-20s %-15s %-10s %-12s %-8s %-5s\n",
                "Name", "Department", "Salary", "Rating", "Years", "Active");
        System.out.println("------------------------------------------");

        for (Employee<T> emp : employees) {
            System.out.printf("%-20s %-15s $%-9.2f %-12.1f %-8d %s\n",
                    emp.getName(),
                    emp.getDepartment(),
                    emp.getSalary(),
                    emp.getPerformanceRating(),
                    emp.getYearsOfExperience(),
                    emp.isActive() ? "Yes" : "No");
        }
        System.out.println("==========================================");
    }

    /**
     * Generate a department summary report using Stream API
     */
    public String generateDepartmentReport() {
        Map<String, List<Employee<T>>> departmentMap = database.getAllEmployees().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));

        if (departmentMap.isEmpty()) {
            return "No employees in any department.";
        }

        StringBuilder report = new StringBuilder();
        report.append("\n=================== Department Report ===================\n");

        departmentMap.forEach((department, empList) -> {
            double avgSalary = empList.stream().mapToDouble(Employee::getSalary).average().orElse(0);
            double avgRating = empList.stream().mapToDouble(Employee::getPerformanceRating).average().orElse(0);
            long activeCount = empList.stream().filter(Employee::isActive).count();

            report.append(String.format("\nDepartment: %s\n", department));
            report.append(String.format("Number of Employees: %d\n", empList.size()));
            report.append(String.format("Active Employees: %d\n", activeCount));
            report.append(String.format("Average Salary: $%.2f\n", avgSalary));
            report.append(String.format("Average Performance Rating: %.2f\n", avgRating));
            report.append("\nEmployees:\n");

            empList.forEach(emp ->
                    report.append(String.format("- %s (Experience: %d years, Rating: %.1f)\n",
                            emp.getName(), emp.getYearsOfExperience(), emp.getPerformanceRating()))
            );

            report.append("---------------------------------------------------\n");
        });

        return report.toString();
    }

    /**
     * Generate a salary distribution report using Stream API
     */
    public String generateSalaryDistributionReport() {
        List<Employee<T>> employees = database.getAllEmployees();

        if (employees.isEmpty()) {
            return "No employees to generate salary distribution.";
        }

        // Define salary ranges
        Map<String, Long> salaryRanges = employees.stream()
                .collect(Collectors.groupingBy(
                        emp -> {
                            double salary = emp.getSalary();
                            if (salary < 50000) return "Below $50,000";
                            else if (salary < 75000) return "$50,000 - $74,999";
                            else if (salary < 100000) return "$75,000 - $99,999";
                            else if (salary < 125000) return "$100,000 - $124,999";
                            else return "$125,000 and above";
                        },
                        Collectors.counting()
                ));

        StringBuilder report = new StringBuilder();
        report.append("\n============= Salary Distribution =============\n");

        // Ensure all ranges are included even if empty
        String[] rangeLabels = {
                "Below $50,000",
                "$50,000 - $74,999",
                "$75,000 - $99,999",
                "$100,000 - $124,999",
                "$125,000 and above"
        };

        for (String label : rangeLabels) {
            long count = salaryRanges.getOrDefault(label, 0L);
            report.append(String.format("%-20s: %d employee(s)\n", label, count));
        }

        report.append("==============================================\n");
        return report.toString();
    }

    /**
     * Generate a performance report using Stream API
     */
    public String generatePerformanceReport() {
        List<Employee<T>> employees = database.getAllEmployees();

        if (employees.isEmpty()) {
            return "No employees to generate performance report.";
        }

        // Group by performance rating ranges
        Map<String, List<Employee<T>>> performanceGroups = employees.stream()
                .collect(Collectors.groupingBy(
                        emp -> {
                            double rating = emp.getPerformanceRating();
                            if (rating < 2.0) return "Poor (0-1.9)";
                            else if (rating < 3.0) return "Fair (2.0-2.9)";
                            else if (rating < 4.0) return "Good (3.0-3.9)";
                            else if (rating < 4.5) return "Excellent (4.0-4.4)";
                            else return "Outstanding (4.5-5.0)";
                        }
                ));

        StringBuilder report = new StringBuilder();
        report.append("\n============= Performance Report =============\n");

        String[] ratingGroups = {
                "Outstanding (4.5-5.0)",
                "Excellent (4.0-4.4)",
                "Good (3.0-3.9)",
                "Fair (2.0-2.9)",
                "Poor (0-1.9)"
        };

        for (String group : ratingGroups) {
            List<Employee<T>> groupEmployees = performanceGroups.getOrDefault(group, List.of());
            report.append(String.format("\n%s: %d employee(s)\n", group, groupEmployees.size()));

            if (!groupEmployees.isEmpty()) {
                report.append("------------------------------------------\n");
                for (Employee<T> emp : groupEmployees) {
                    report.append(String.format("- %s (Dept: %s, Rating: %.1f)\n",
                            emp.getName(), emp.getDepartment(), emp.getPerformanceRating()));
                }
            }
        }

        report.append("==============================================\n");
        return report.toString();
    }
}