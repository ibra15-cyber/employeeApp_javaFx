package com.ibra.employeeapplication.backend.service;

import com.ibra.employeeapplication.backend.entity.Employee;
import com.ibra.employeeapplication.backend.exception.EmployeeNotFoundException;
import com.ibra.employeeapplication.backend.exception.InvalidDepartmentException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EmployeeSearchEngine<T> implements Iterable<Employee<T>> {
    private EmployeeDB<T> database;
    private static final Logger logger = Logger.getLogger(EmployeeSearchEngine.class.getName());


    public EmployeeSearchEngine(EmployeeDB<T> database) {
        if (database == null) {
            throw new IllegalArgumentException("Database cannot be null");
        }
        this.database = database;
    }

    // Previously implemented search methods remain the same...
    public List<Employee<T>> findByDepartment(String department) throws InvalidDepartmentException {
        try {
            if (department == null || department.isEmpty()) {
                throw new InvalidDepartmentException("Department cannot be null or empty");
            }

            List<Employee<T>> results = database.getAllEmployees().stream()
                    .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                    .collect(Collectors.toList());

            logger.info("Found " + results.size() + " employees in department: " + department);
            return results;
        } catch (InvalidDepartmentException e) {
            logger.warning("Invalid department search: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Error searching by department: " + e.getMessage());
            throw new RuntimeException("Error searching by department", e);
        }

    }

    public List<Employee<T>> findByName(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.isEmpty()) {
                throw new EmployeeNotFoundException("Employee not found");
            }
            List<Employee<T>> results = database.getAllEmployees().stream()
                    .filter(emp -> emp.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                    .collect(Collectors.toList());
            logger.info("Found " + results.size() + " employees in name: " + searchTerm);
            return results;
        } catch (EmployeeNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Employee<T>> findByMinimumRating(double minRating) {
        try {
            if (minRating < 0 || minRating > 5 || Double.isNaN(minRating)) {
                throw new IllegalArgumentException("Rating must be between 0 and 5");
            }
            List<Employee<T>> results = database.getAllEmployees().stream()
                    .filter(emp -> emp.getPerformanceRating() >= minRating)
                    .collect(Collectors.toList());
            logger.info("Found " + results.size() + " employees with rating >= " + minRating);
            return results;
        } catch (Exception e){
            logger.severe("Error searching by performance rating: " + e.getMessage());
            throw new RuntimeException("Error searching by performance rating", e);
        }
    }

    public List<Employee<T>> findBySalaryRange(double minSalary, double maxSalary) {
        return database.getAllEmployees().stream()
                .filter(emp -> emp.getSalary() >= minSalary && emp.getSalary() <= maxSalary)
                .collect(Collectors.toList());
    }

    public List<Employee<T>> findActiveEmployees() {
        try {
            if (database.getAllEmployees().isEmpty()) {
                throw new RuntimeException("Employee list is empty");
            }

            return database.getAllEmployees().stream()
                    .filter(Employee::isActive)
                    .collect(Collectors.toList());
        } catch (Exception e){
            throw new RuntimeException("Error searching by active employees", e);
        }

    }


    // NEW SORTING METHODS      * Replaced by a different class

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