package com.ibra.employeeapplication.backend.service;

import com.ibra.employeeapplication.backend.entity.Employee;

import java.util.Comparator;

/**
 * Comparator to sort employees by salary in descending order (highest first)
 */
public class EmployeeSalaryComparator<T> implements Comparator<Employee<T>> {
    @Override
    public int compare(Employee<T> emp1, Employee<T> emp2) {
        // For descending order (highest first)
        return Double.compare(emp2.getSalary(), emp1.getSalary());
    }
}


/**
 * Comparator to sort employees by department name in alphabetical order
 */
class EmployeeDepartmentComparator<T> implements Comparator<Employee<T>> {
    @Override
    public int compare(Employee<T> emp1, Employee<T> emp2) {
        return emp1.getDepartment().compareTo(emp2.getDepartment());
    }
}

/**
 * Comparator to sort employees by name in alphabetical order
 */
class EmployeeNameComparator<T> implements Comparator<Employee<T>> {
    @Override
    public int compare(Employee<T> emp1, Employee<T> emp2) {
        return emp1.getName().compareTo(emp2.getName());
    }
}

/**
 * Utility class for creating custom compound comparators
 */
class EmployeeComparatorFactory {

    /**
     * Create a comparator that sorts by department first, then by salary (highest first)
     */
    public static <T> Comparator<Employee<T>> byDepartmentThenSalary() {
        return new EmployeeDepartmentComparator<T>()
                .thenComparing(new EmployeeSalaryComparator<T>());
    }

    /**
     * Create a comparator that sorts by performance first, then by years of experience
     */
    public static <T> Comparator<Employee<T>> byPerformanceThenExperience() {
        return new EmployeePerformanceComparator<T>()
                .thenComparing((e1, e2) -> Integer.compare(e2.getYearsOfExperience(),
                        e1.getYearsOfExperience()));
    }
}