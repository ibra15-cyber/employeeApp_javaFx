package com.ibra.employeeapplication.backend.service;

import com.ibra.employeeapplication.backend.entity.Employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeSearchEngine<T> implements Iterable<Employee<T>> {
    private EmployeeDB<T> database;

    public EmployeeSearchEngine(EmployeeDB<T> database) {
        this.database = database;
    }

    // Previously implemented search methods remain the same...
    public List<Employee<T>> findByDepartment(String department) {
        return database.getAllEmployees().stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    public List<Employee<T>> findByName(String searchTerm) {
        return database.getAllEmployees().stream()
                .filter(emp -> emp.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Employee<T>> findByMinimumRating(double minRating) {
        return database.getAllEmployees().stream()
                .filter(emp -> emp.getPerformanceRating() >= minRating)
                .collect(Collectors.toList());
    }

    public List<Employee<T>> findBySalaryRange(double minSalary, double maxSalary) {
        return database.getAllEmployees().stream()
                .filter(emp -> emp.getSalary() >= minSalary && emp.getSalary() <= maxSalary)
                .collect(Collectors.toList());
    }

    public List<Employee<T>> findActiveEmployees() {
        return database.getAllEmployees().stream()
                .filter(Employee::isActive)
                .collect(Collectors.toList());
    }

    // NEW SORTING METHODS

    /**
     * Get all employees sorted by their natural ordering (years of experience)
     * Uses the Comparable implementation in Employee class
     * @return List of employees sorted by years of experience (most experienced first)
     */
    public List<Employee<T>> getAllEmployeesByExperience() {
        List<Employee<T>> employees = new ArrayList<>(database.getAllEmployees());
        Collections.sort(employees);  // Uses Employee's natural ordering (Comparable)
        return employees;
    }

    /**
     * Get all employees sorted by salary (highest first)
     * Uses the EmployeeSalaryComparator
     * @return List of employees sorted by salary
     */
    public List<Employee<T>> getAllEmployeesBySalary() {
        List<Employee<T>> employees = new ArrayList<>(database.getAllEmployees());
        employees.sort(new EmployeeSalaryComparator<>());
        return employees;
    }

    /**
     * Get all employees sorted by performance rating (best first)
     * Uses the EmployeePerformanceComparator
     * @return List of employees sorted by performance rating
     */
    public List<Employee<T>> getAllEmployeesByPerformance() {
        List<Employee<T>> employees = new ArrayList<>(database.getAllEmployees());
        employees.sort(new EmployeePerformanceComparator<>());
        return employees;
    }

    /**
     * Sort employees by department first, then by salary within each department
     * @return List of employees sorted by department and salary
     */
    public List<Employee<T>> getAllEmployeesByDepartmentAndSalary() {
        List<Employee<T>> employees = new ArrayList<>(database.getAllEmployees());
        employees.sort(EmployeeComparatorFactory.byDepartmentThenSalary());
        return employees;
    }

    /**
     * Sort employees by performance rating first, then by years of experience
     * @return List of employees sorted by performance and experience
     */
    public List<Employee<T>> getAllEmployeesByPerformanceAndExperience() {
        List<Employee<T>> employees = new ArrayList<>(database.getAllEmployees());
        employees.sort(EmployeeComparatorFactory.byPerformanceThenExperience());
        return employees;
    }

    /**
     * Generic method to sort employees by any custom comparator
     * @param comparator The comparator to use for sorting
     * @return Sorted list of employees
     */
    public List<Employee<T>> sortEmployees(Comparator<Employee<T>> comparator) {
        List<Employee<T>> employees = new ArrayList<>(database.getAllEmployees());
        employees.sort(comparator);
        return employees;
    }

    // Existing utility methods...
    public Map<String, Long> getDepartmentCounts() {
        return database.getAllEmployees().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
    }

    public double getAverageSalary() {
        return database.getAllEmployees().stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    public Employee<T> findTopPerformer() {
        return database.getAllEmployees().stream()
                .max((e1, e2) -> Double.compare(e1.getPerformanceRating(), e2.getPerformanceRating()))
                .orElse(null);
    }

    public void displaySearchResults(List<Employee<T>> employees) {
        if (employees.isEmpty()) {
            System.out.println("No matching employees found.");
            return;
        }

        System.out.println("===== Search Results =====");
        System.out.println("Found " + employees.size() + " matching employees:");
        employees.forEach(System.out::println);
        System.out.println("=========================");
    }

    @Override
    public Iterator<Employee<T>> iterator() {
        return new EmployeeIterator();
    }

    private class EmployeeIterator implements Iterator<Employee<T>> {
        private Iterator<Employee<T>> iterator;

        public EmployeeIterator() {
            this.iterator = database.getAllEmployees().iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Employee<T> next() {
            return iterator.next();
        }
    }
}