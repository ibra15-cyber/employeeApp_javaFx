package com.ibra.employeeapplication.backend.entity;

import com.ibra.employeeapplication.backend.exception.EmployeeNotFoundException;
import com.ibra.employeeapplication.backend.exception.InvalidDepartmentException;
import com.ibra.employeeapplication.backend.exception.InvalidSalaryException;

import javax.naming.InvalidNameException;

//import javax.naming.InvalidNameException;

public class Employee<T> implements Comparable<Employee<T>> {
    private T employeeId;
    private String name;
    private String department;
    private double salary;
    private double performanceRating;
    private int yearsOfExperience;
    private boolean active;

    public Employee(T employeeId, boolean active,
                    int yearOfExperience, double salary,
                    double performanceRatings,
                    String department, String name) throws InvalidDepartmentException, InvalidSalaryException, EmployeeNotFoundException {

        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new EmployeeNotFoundException("Employee name cannot be empty");
        }

        if (department == null || department.trim().isEmpty()) {
            throw new InvalidDepartmentException("Department cannot be empty");
        }

        if (salary < 0) {
            throw new InvalidSalaryException("Salary cannot be negative");
        }

        if (performanceRating < 0 || performanceRating > 5) {
            throw new IllegalArgumentException("Performance rating must be between 0 and 5");
        }

        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Years of experience cannot be negative");
        }

        this.employeeId = employeeId;
        this.active = active;
        this.yearsOfExperience = yearOfExperience;
        this.salary = salary;
        this.performanceRating = performanceRatings;
        this.department = department;
        this.name = name;
    }

    public T getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(T employeeId) {
        this.employeeId = employeeId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) throws IllegalArgumentException {
        //if name is invalid
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be empty");
        }

        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) throws InvalidDepartmentException {
        if (department == null || department.trim().isEmpty()) {
            throw new InvalidDepartmentException("Department cannot be empty");
        }
        this.department = department;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) throws InvalidSalaryException {
        //check if salary is negative
        if (salary < 0) {
            throw new InvalidSalaryException("Salary cannot be negative");
        }

        this.salary = salary;
    }

    public double getPerformanceRating() {
        return performanceRating;
    }

    public void setPerformanceRating(double performanceRating) {
        this.performanceRating = performanceRating;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public int compareTo(Employee<T> other) {
        return Integer.compare(this.getYearsOfExperience(), other.getYearsOfExperience());
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", salary=" + salary +
                ", performanceRatings=" + performanceRating +
                ", yearOfExperience=" + yearsOfExperience +
                ", active=" + active +
                '}';
    }
}
