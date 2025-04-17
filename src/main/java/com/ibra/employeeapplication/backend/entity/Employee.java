package com.ibra.employeeapplication.backend.entity;

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
                    String department, String name) {
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

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
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
