package com.ibra.employeeapplication.backend.test;

import com.ibra.employeeapplication.backend.entity.Employee;
import com.ibra.employeeapplication.backend.exception.EmployeeNotFoundException;
import com.ibra.employeeapplication.backend.exception.InvalidDepartmentException;
import com.ibra.employeeapplication.backend.exception.InvalidSalaryException;
import com.ibra.employeeapplication.backend.service.EmployeeDB;
import com.ibra.employeeapplication.backend.service.EmployeeSearchEngine;
import com.ibra.employeeapplication.backend.service.SalaryManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

public class EmployeeManagementSystemTest {

    private EmployeeDB<Integer> database;
    private EmployeeSearchEngine<Integer> searchEngine;
    private SalaryManager<Integer> salaryManager;

    @Before
    public void setUp() {
        // Initialize your system
        database = new EmployeeDB<>();
        searchEngine = new EmployeeSearchEngine<>(database);
        salaryManager = new SalaryManager<>(database);

        // Add some test employees
        try {
            database.addEmployee(new Employee<>(1, true, 5, 50000.0, 4.2, "IT", "John Doe"));
            database.addEmployee(new Employee<>(2, true, 3, 42000.0, 3.8, "HR", "Jane Smith"));
            database.addEmployee(new Employee<>(3, true, 7, 65000.0, 4.5, "IT", "Bob Johnson"));
            database.addEmployee(new Employee<>(4, false, 2, 38000.0, 3.2, "Sales", "Alice Brown"));
        } catch (Exception e) {
            fail("Exception during setup: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        // Clean up resources if needed
    }

    @Test
    public void testAddEmployee() {
        try {
            // Test adding a new employee
            Employee<Integer> newEmployee = new Employee<>(5, true, 4, 48000.0, 4.0, "Marketing", "Chris Evans");
            boolean result = database.addEmployee(newEmployee);
            assertTrue("Should successfully add new employee", result);
            assertEquals("Database should now have 5 employees", 5, database.getEmployeeCount());

            // Test adding a duplicate employee
            result = database.addEmployee(newEmployee);
            assertFalse("Should not add duplicate employee", result);
        } catch (Exception e) {
            fail("Exception during test: " + e.getMessage());
        }
    }

    @Test
    public void testSearchByDepartment() {
        try {
            // Test searching for IT department
            List<Employee<Integer>> itEmployees = searchEngine.findByDepartment("IT");
            assertEquals("Should find 2 IT employees", 2, itEmployees.size());

            // Test searching for non-existent department
            List<Employee<Integer>> noEmployees = searchEngine.findByDepartment("Finance");
            assertTrue("Should find no employees in Finance", noEmployees.isEmpty());
        } catch (Exception e) {
            fail("Exception during test: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteEmployee() {
        try {
            // Test deleting an existing employee
            boolean result = database.removeEmployee(2);
            assertTrue("Should successfully remove employee with ID 2", result);
            assertEquals("Database should now have 3 employees", 3, database.getEmployeeCount());

            // Test trying to get deleted employee (should throw exception)
            database.getEmployeeById(2);
        } catch (Exception e) {
            fail("Exception during test: " + e.getMessage());
        }
    }

    @Test
    public void testSalaryRaiseByDepartment() {
        try {
            // Record IT employees' salaries before raise
            List<Employee<Integer>> itEmployeesBefore = searchEngine.findByDepartment("IT");
            Map<Integer, Double> originalSalaries = new HashMap<>();

            System.out.println("Original salaries for IT department:");
            for (Employee<Integer> emp : itEmployeesBefore) {
                originalSalaries.put(emp.getEmployeeId(), emp.getSalary());
                System.out.println("Employee ID: " + emp.getEmployeeId() +
                        ", Name: " + emp.getName() +
                        ", Original Salary: $" + String.format("%.2f", emp.getSalary()));
            }

            // Give 10% raise to IT department
            int raisedCount = salaryManager.giveSalaryRaiseByDepartment("IT", 10.0);
            assertEquals("Should raise 2 IT employees' salaries", 2, raisedCount);

            // Verify new salaries
            System.out.println("\nUpdated salaries after 10% raise:");
            List<Employee<Integer>> itEmployeesAfter = searchEngine.findByDepartment("IT");
            for (Employee<Integer> emp : itEmployeesAfter) {
                double originalSalary = originalSalaries.get(emp.getEmployeeId());
                double expectedSalary = originalSalary * 1.1;
                double actualSalary = emp.getSalary();

                System.out.println("Employee ID: " + emp.getEmployeeId() +
                        ", Name: " + emp.getName() +
                        ", Original: $" + String.format("%.2f", originalSalary) +
                        ", New: $" + String.format("%.2f", actualSalary) +
                        ", Increase: $" + String.format("%.2f", (actualSalary - originalSalary)));

                assertEquals("Salary should be increased by 10%", expectedSalary, actualSalary, 0.01);
            }
        } catch (Exception e) {
            fail("Exception during test: " + e.getMessage());
        }
    }

    @Test(expected = InvalidSalaryException.class)
    public void testInvalidSalaryException() throws InvalidSalaryException {
        // Test that negative salary throws exception
        Employee<Integer> employee = null;
        try {
            employee = new Employee<>(6, true, 3, -5000.0, 3.5, "Finance", "Test User");
            fail("Should throw InvalidSalaryException for negative salary");
        } catch (InvalidSalaryException e) {
            // Print the error message if InvalidSalaryException is caught
            System.out.println("Caught InvalidSalaryException: " + e.getMessage());
            e.printStackTrace();  // Optionally, print the stack trace for debugging
            throw e;  // Rethrow the exception since we're expecting it
        } catch (InvalidDepartmentException | EmployeeNotFoundException e) {
            // This should not happen in this test, so we print and fail the test if it occurs
            System.out.println("Unexpected exception caught: " + e.getClass().getName() + " - " + e.getMessage());
            fail("Should not throw department or employee exception");
        }
    }


    // Add more tests for other functionality
}