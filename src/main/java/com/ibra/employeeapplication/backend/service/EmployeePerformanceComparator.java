package com.ibra.employeeapplication.backend.service;

import com.ibra.employeeapplication.backend.entity.Employee;

import java.util.Comparator; /**
 * Comparator to sort employees by performance rating in descending order (best first)
 */
public class EmployeePerformanceComparator<T> implements Comparator<Employee<T>> {
    @Override
    public int compare(Employee<T> emp1, Employee<T> emp2) {
        // For descending order (highest rating first)
        return Double.compare(emp2.getPerformanceRating(), emp1.getPerformanceRating());
    }
}
