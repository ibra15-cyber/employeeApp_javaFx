package com.ibra.employeeapplication.backend.service;

import com.ibra.employeeapplication.backend.entity.Employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDB<T> {
    // HashMap to store employees with employeeId as key
    private Map<T, Employee<T>> employees;

    // Constructor
    public EmployeeDB() {
        this.employees = new HashMap<>();
    }

    /**
     * Add a new employee to the database
     * @param employee The employee to add
     * @return true if added successfully, false if employee with same ID already exists
     */
    public boolean addEmployee(Employee<T> employee) {
        if (employees.containsKey(employee.getEmployeeId())) {
            System.out.println("Employee with ID " + employee.getEmployeeId() + " already exists.");
            return false;
        }

        employees.put(employee.getEmployeeId(), employee);
        System.out.println("Employee added successfully: " + employee.getName());
        return true;
    }

    /**
     * Remove an employee from the database
     * @param employeeId The ID of the employee to remove
     * @return true if removed successfully, false if employee wasn't found
     */
    public boolean removeEmployee(T employeeId) {
        if (!employees.containsKey(employeeId)) {
            System.out.println("Employee with ID " + employeeId + " not found.");
            return false;
        }

        Employee<T> removedEmployee = employees.remove(employeeId);
        System.out.println("Employee removed successfully: " + removedEmployee.getName());
        return true;
    }

    /**
     * Update an employee's details dynamically based on field name
     * @param employeeId The ID of the employee to update
     * @param field The field to update (name, department, salary, etc.)
     * @param newValue The new value for the field
     * @return true if updated successfully, false otherwise
     */
    public boolean updateEmployeeDetails(T employeeId, String field, Object newValue) {
        if (!employees.containsKey(employeeId)) {
            System.out.println("Employee with ID " + employeeId + " not found.");
            return false;
        }

        Employee<T> employee = employees.get(employeeId);

        try {
            switch (field.toLowerCase()) {
                case "name":
                    if (newValue instanceof String) {
                        employee.setName((String) newValue);
                    } else {
                        throw new IllegalArgumentException("Name must be a String");
                    }
                    break;
                case "department":
                    if (newValue instanceof String) {
                        employee.setDepartment((String) newValue);
                    } else {
                        throw new IllegalArgumentException("Department must be a String");
                    }
                    break;
                case "salary":
                    if (newValue instanceof Double) {
                        employee.setSalary((Double) newValue);
                    } else if (newValue instanceof Integer) {
                        employee.setSalary(((Integer) newValue).doubleValue());
                    } else {
                        throw new IllegalArgumentException("Salary must be a numeric value");
                    }
                    break;
                case "performancerating":
                    if (newValue instanceof Double) {
                        double rating = (Double) newValue;
                        if (rating < 0 || rating > 5) {
                            throw new IllegalArgumentException("Performance rating must be between 0 and 5");
                        }
                        employee.setPerformanceRating(rating);
                    } else if (newValue instanceof Integer) {
                        int rating = (Integer) newValue;
                        if (rating < 0 || rating > 5) {
                            throw new IllegalArgumentException("Performance rating must be between 0 and 5");
                        }
                        employee.setPerformanceRating(rating);
                    } else {
                        throw new IllegalArgumentException("Performance rating must be a numeric value");
                    }
                    break;
                case "yearsofexperience":
                    if (newValue instanceof Integer) {
                        employee.setYearsOfExperience((Integer) newValue);
                    } else if (newValue instanceof Double) {
                        employee.setYearsOfExperience(((Double) newValue).intValue());
                    } else {
                        throw new IllegalArgumentException("Years of experience must be a numeric value");
                    }
                    break;
                case "isactive":
                    if (newValue instanceof Boolean) {
                        employee.setActive((Boolean) newValue);
                    } else if (newValue instanceof String) {
                        employee.setActive(Boolean.parseBoolean((String) newValue));
                    } else {
                        throw new IllegalArgumentException("Active status must be a boolean value");
                    }
                    break;
                default:
                    System.out.println("Invalid field: " + field);
                    return false;
            }

            System.out.println("Employee " + employee.getName() + " updated successfully. Field: " + field);
            return true;

        } catch (IllegalArgumentException e) {
            System.out.println("Error updating employee: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all employees in the database
     * @return List of all employees
     */
    public List<Employee<T>> getAllEmployees() {
        if (employees.isEmpty()) {
            System.out.println("No employees in the database.");
            return new ArrayList<>();
        }

        List<Employee<T>> employeeList = new ArrayList<>(employees.values());
        return employeeList;
    }

    /**
     * Get a sorted list of all employees (sorted by years of experience)
     * @return Sorted list of employees
     */
    public List<Employee<T>> getAllEmployeesSorted() {
        List<Employee<T>> employeeList = getAllEmployees();
        Collections.sort(employeeList);
        return employeeList;
    }

    /**
     * Get employee by ID
     * @param employeeId The ID of the employee to retrieve
     * @return The employee object or null if not found
     */
    public Employee<T> getEmployeeById(T employeeId) {
        return employees.get(employeeId);
    }

    /**
     * Get the current number of employees in the database
     * @return Number of employees
     */
    public int getEmployeeCount() {
        return employees.size();
    }

    /**
     * Display all employees in the database
     */
    public void displayAllEmployees() {
        if (employees.isEmpty()) {
            System.out.println("No employees in the database.");
            return;
        }

        System.out.println("===== All Employees =====");
        for (Employee<T> employee : employees.values()) {
            System.out.println(employee);
        }
        System.out.println("========================");
    }
}
