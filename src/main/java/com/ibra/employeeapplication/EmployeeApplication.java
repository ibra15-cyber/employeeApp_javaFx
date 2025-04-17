package com.ibra.employeeapplication;

import com.ibra.employeeapplication.backend.controller.EmployeeDisplay;
import com.ibra.employeeapplication.backend.entity.Employee;
import com.ibra.employeeapplication.backend.service.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.*;

public class EmployeeApplication extends Application {

    // Database and supporting components
    private EmployeeDB<UUID> database;
    private EmployeeSearchEngine<UUID> searchEngine;
    private SalaryManager<UUID> salaryManagement;
    private EmployeeDisplay<UUID> employeeDisplay;

    // UI Components
    private TableView<Employee<UUID>> employeeTable;
    private ObservableList<Employee<UUID>> employeeData;
    private TextArea outputArea;

    @Override
    public void start(Stage primaryStage) {
        // Initialize components
        database = new EmployeeDB();
        searchEngine = new EmployeeSearchEngine<>(database);
        salaryManagement = new SalaryManager<>(database);
        employeeDisplay = new EmployeeDisplay<>(database);

        // Add sample data
        addSampleData();

        // Create the main layout
        BorderPane mainLayout = new BorderPane();

        // Create the top menu
        mainLayout.setTop(createMenuBar());

        // Create the center content with employee table
        mainLayout.setCenter(createCenterContent());

        // Create the right panel with actions
        mainLayout.setRight(createRightPanel());

        // Set the main scene
        Scene scene = new Scene(mainLayout, 1024, 768);
        primaryStage.setTitle("Employee Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exitItem);

        // Employee menu
        Menu employeeMenu = new Menu("Employees");
        MenuItem refreshItem = new MenuItem("Refresh");
        refreshItem.setOnAction(e -> refreshEmployeeTable());
        MenuItem addItem = new MenuItem("Add New Employee");
        addItem.setOnAction(e -> showAddEmployeeDialog());
        employeeMenu.getItems().addAll(refreshItem, addItem);

        // Reports menu
        Menu reportsMenu = new Menu("Reports");
        MenuItem deptReportItem = new MenuItem("Department Report");
        deptReportItem.setOnAction(e -> outputArea.setText(employeeDisplay.generateDepartmentReport()));
        MenuItem salaryReportItem = new MenuItem("Salary Distribution");
        salaryReportItem.setOnAction(e -> outputArea.setText(employeeDisplay.generateSalaryDistributionReport()));
        MenuItem perfReportItem = new MenuItem("Performance Report");
        perfReportItem.setOnAction(e -> outputArea.setText(employeeDisplay.generatePerformanceReport()));
        reportsMenu.getItems().addAll(deptReportItem, salaryReportItem, perfReportItem);

        menuBar.getMenus().addAll(fileMenu, employeeMenu, reportsMenu);
        return menuBar;
    }

    private VBox createCenterContent() {
        VBox centerContent = new VBox(10);
        centerContent.setPadding(new Insets(10));

        // Label
        Label label = new Label("Employee List");
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Search box
        HBox searchBox = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name...");
        searchField.setPrefWidth(250);
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String term = searchField.getText().trim();
            if (!term.isEmpty()) {
                List<Employee<UUID>> results = searchEngine.findByName(term);
                employeeData.setAll(results);
            } else {
                refreshEmployeeTable();
            }
        });
        searchBox.getChildren().addAll(new Label("Search:"), searchField, searchButton);

        // Create employee table
        createEmployeeTable();

        // Output area for reports
        outputArea = new TextArea();
        outputArea.setPrefHeight(200);
        outputArea.setEditable(false);
        outputArea.setPromptText("Reports will be displayed here...");

        centerContent.getChildren().addAll(label, searchBox, employeeTable, outputArea);
        return centerContent;
    }

    private void createEmployeeTable() {
        employeeTable = new TableView<>();
        employeeData = FXCollections.observableArrayList(database.getAllEmployees());

        // Define columns
        TableColumn<Employee<UUID>, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(120);

        TableColumn<Employee<UUID>, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDepartment()));
        deptCol.setPrefWidth(100);

        TableColumn<Employee<UUID>, String> salaryCol = new TableColumn<>("Salary");
        salaryCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getSalary())));
        salaryCol.setPrefWidth(100);

        TableColumn<Employee<UUID>, String> ratingCol = new TableColumn<>("Rating");
        ratingCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.1f", cellData.getValue().getPerformanceRating())));
        ratingCol.setPrefWidth(60);

        TableColumn<Employee<UUID>, String> experienceCol = new TableColumn<>("Years Exp.");
        experienceCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(Integer.toString(cellData.getValue().getYearsOfExperience())));
        experienceCol.setPrefWidth(80);

        TableColumn<Employee<UUID>, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isActive() ? "Active" : "Inactive"));
        statusCol.setPrefWidth(80);

        // Add action column for edit/delete
        TableColumn<Employee<UUID>, Void> actionCol = new TableColumn<>("Actions");

        Callback<TableColumn<Employee<UUID>, Void>, TableCell<Employee<UUID>, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<Employee<UUID>, Void> call(final TableColumn<Employee<UUID>, Void> param) {
                        return new TableCell<>() {
                            private final Button editBtn = new Button("Edit");
                            private final Button deleteBtn = new Button("Delete");
                            private final HBox pane = new HBox(5, editBtn, deleteBtn);

                            {
                                editBtn.setOnAction(event -> {
                                    Employee<UUID> employee = getTableView().getItems().get(getIndex());
                                    showEditEmployeeDialog(employee);
                                });

                                deleteBtn.setOnAction(event -> {
                                    Employee<UUID> employee = getTableView().getItems().get(getIndex());
                                    database.removeEmployee(employee.getEmployeeId());
                                    refreshEmployeeTable();
                                });
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                setGraphic(empty ? null : pane);
                            }
                        };
                    }
                };

        actionCol.setCellFactory(cellFactory);
        actionCol.setPrefWidth(140);

        employeeTable.getColumns().addAll(nameCol, deptCol, salaryCol, ratingCol,
                experienceCol, statusCol, actionCol);
        employeeTable.setItems(employeeData);
    }

    private VBox createRightPanel() {
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setPrefWidth(200);
        rightPanel.setStyle("-fx-background-color: #f0f0f0;");

        Label actionsLabel = new Label("Actions");
        actionsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button addButton = new Button("Add Employee");
        addButton.setPrefWidth(180);
        addButton.setOnAction(e -> showAddEmployeeDialog());

        Label sortLabel = new Label("Sort Options");
        sortLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ComboBox<String> sortOptions = new ComboBox<>();
        sortOptions.getItems().addAll(
                "By Experience (Default)",
                "By Salary (Highest First)",
                "By Performance (Best First)"
        );
        sortOptions.setValue("By Experience (Default)");
        sortOptions.setPrefWidth(180);

        Button sortButton = new Button("Apply Sort");
        sortButton.setPrefWidth(180);
        sortButton.setOnAction(e -> {
            String selected = sortOptions.getValue();
            List<Employee<UUID>> sortedList;

            if (selected.contains("Salary")) {
                sortedList = database.getAllEmployees();
                sortedList.sort(new EmployeeSalaryComparator<>());
            } else if (selected.contains("Performance")) {
                sortedList = database.getAllEmployees();
                sortedList.sort(new EmployeePerformanceComparator<>());
            } else {
                // Default sort by experience
                sortedList = database.getAllEmployeesSorted();
            }

            employeeData.setAll(sortedList);
        });

        Label filterLabel = new Label("Filters");
        filterLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ComboBox<String> departmentFilter = new ComboBox<>();
        departmentFilter.getItems().add("All Departments");
        // Add departments dynamically
        Set<String> departments = new HashSet<>();
        database.getAllEmployees().forEach(emp -> departments.add(emp.getDepartment()));
        departmentFilter.getItems().addAll(departments);
        departmentFilter.setValue("All Departments");
        departmentFilter.setPrefWidth(180);

        Slider ratingFilter = new Slider(0, 5, 0);
        ratingFilter.setShowTickLabels(true);
        ratingFilter.setShowTickMarks(true);
        ratingFilter.setMajorTickUnit(1);
        ratingFilter.setBlockIncrement(0.5);

        Button applyFilterButton = new Button("Apply Filters");
        applyFilterButton.setPrefWidth(180);
        applyFilterButton.setOnAction(e -> {
            String dept = departmentFilter.getValue();
            double minRating = ratingFilter.getValue();

            List<Employee<UUID>> filteredList = database.getAllEmployees();

            // Apply department filter if not "All Departments"
            if (!dept.equals("All Departments")) {
                filteredList = filteredList.stream()
                        .filter(emp -> emp.getDepartment().equals(dept))
                        .toList();
            }

            // Apply rating filter if above 0
            if (minRating > 0) {
                final double finalMinRating = minRating;
                filteredList = filteredList.stream()
                        .filter(emp -> emp.getPerformanceRating() >= finalMinRating)
                        .toList();
            }

            employeeData.setAll(filteredList);
        });

        Button resetButton = new Button("Reset All Filters");
        resetButton.setPrefWidth(180);
        resetButton.setOnAction(e -> {
            departmentFilter.setValue("All Departments");
            ratingFilter.setValue(0);
            refreshEmployeeTable();
        });

        // Salary management section
        Label salaryLabel = new Label("Salary Management");
        salaryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button topPaidButton = new Button("Show Top 5 Paid");
        topPaidButton.setPrefWidth(180);
        topPaidButton.setOnAction(e -> {
            List<Employee<UUID>> topPaid = salaryManagement.getTopPaidEmployees(5);
            employeeData.setAll(topPaid);
            outputArea.setText("Showing top 5 highest paid employees");
        });

        Button raiseButton = new Button("Give Raises (Rating ≥ 4.5)");
        raiseButton.setPrefWidth(180);
        raiseButton.setOnAction(e -> {
            int raisesGiven = salaryManagement.giveSalaryRaiseByPerformance(4.5, 5.0);
            refreshEmployeeTable();
            outputArea.setText("Gave 5% raise to " + raisesGiven + " high-performing employees");
        });

        rightPanel.getChildren().addAll(
                actionsLabel, addButton,
                sortLabel, sortOptions, sortButton,
                filterLabel, new Label("Department:"), departmentFilter,
                new Label("Min Rating:"), ratingFilter,
                applyFilterButton, resetButton,
                salaryLabel, topPaidButton, raiseButton
        );

        return rightPanel;
    }

    private void showAddEmployeeDialog() {
        Dialog<Employee<UUID>> dialog = new Dialog<>();
        dialog.setTitle("Add New Employee");
        dialog.setHeaderText("Enter employee details");

        // Set button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        ComboBox<String> departmentField = new ComboBox<>();
        departmentField.getItems().addAll("IT", "HR", "Finance", "Marketing", "Sales", "Operations");
        departmentField.setPromptText("Department");
        departmentField.setEditable(true);

        TextField salaryField = new TextField();
        salaryField.setPromptText("Salary");

        Slider ratingSlider = new Slider(0, 5, 3);
        ratingSlider.setShowTickLabels(true);
        ratingSlider.setShowTickMarks(true);
        ratingSlider.setMajorTickUnit(1);
        ratingSlider.setBlockIncrement(0.5);

        TextField yearsField = new TextField();
        yearsField.setPromptText("Years of Experience");

        CheckBox activeCheck = new CheckBox("Active Employee");
        activeCheck.setSelected(true);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Department:"), 0, 1);
        grid.add(departmentField, 1, 1);
        grid.add(new Label("Salary:"), 0, 2);
        grid.add(salaryField, 1, 2);
        grid.add(new Label("Performance Rating:"), 0, 3);
        grid.add(ratingSlider, 1, 3);
        grid.add(new Label("Years of Experience:"), 0, 4);
        grid.add(yearsField, 1, 4);
        grid.add(activeCheck, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText();
                    String department = departmentField.getValue();
                    double salary = Double.parseDouble(salaryField.getText());
                    double rating = ratingSlider.getValue();
                    int years = Integer.parseInt(yearsField.getText());
                    boolean active = activeCheck.isSelected();

                    return new Employee<>(
                            UUID.randomUUID(), active, years, salary, rating, department, name
                    );
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Employee<UUID>> result = dialog.showAndWait();
        result.ifPresent(employee -> {
            database.addEmployee(employee);
            refreshEmployeeTable();
        });
    }

    private void showEditEmployeeDialog(Employee<UUID> employee) {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Edit Employee");
        dialog.setHeaderText("Edit details for: " + employee.getName());

        // Set button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(employee.getName());

        ComboBox<String> departmentField = new ComboBox<>();
        departmentField.getItems().addAll("IT", "HR", "Finance", "Marketing", "Sales", "Operations");
        departmentField.setValue(employee.getDepartment());
        departmentField.setEditable(true);

        TextField salaryField = new TextField(String.valueOf(employee.getSalary()));

        Slider ratingSlider = new Slider(0, 5, employee.getPerformanceRating());
        // Continue from previous implementation...
        ratingSlider.setShowTickLabels(true);
        ratingSlider.setShowTickMarks(true);
        ratingSlider.setMajorTickUnit(1);
        ratingSlider.setBlockIncrement(0.5);

        TextField yearsField = new TextField(String.valueOf(employee.getYearsOfExperience()));

        CheckBox activeCheck = new CheckBox("Active Employee");
        activeCheck.setSelected(employee.isActive());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Department:"), 0, 1);
        grid.add(departmentField, 1, 1);
        grid.add(new Label("Salary:"), 0, 2);
        grid.add(salaryField, 1, 2);
        grid.add(new Label("Performance Rating:"), 0, 3);
        grid.add(ratingSlider, 1, 3);
        grid.add(new Label("Years of Experience:"), 0, 4);
        grid.add(yearsField, 1, 4);
        grid.add(activeCheck, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Map<String, Object> result = new HashMap<>();
                    result.put("name", nameField.getText());
                    result.put("department", departmentField.getValue());
                    result.put("salary", Double.parseDouble(salaryField.getText()));
                    result.put("performanceRating", ratingSlider.getValue());
                    result.put("yearsOfExperience", Integer.parseInt(yearsField.getText()));
                    result.put("isActive", activeCheck.isSelected());
                    return result;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Map<String, Object>> result = dialog.showAndWait();
        result.ifPresent(updates -> {
            // Update all fields
            database.updateEmployeeDetails(employee.getEmployeeId(), "name", updates.get("name"));
            database.updateEmployeeDetails(employee.getEmployeeId(), "department", updates.get("department"));
            database.updateEmployeeDetails(employee.getEmployeeId(), "salary", updates.get("salary"));
            database.updateEmployeeDetails(employee.getEmployeeId(), "performanceRating", updates.get("performanceRating"));
            database.updateEmployeeDetails(employee.getEmployeeId(), "yearsOfExperience", updates.get("yearsOfExperience"));
            database.updateEmployeeDetails(employee.getEmployeeId(), "isActive", updates.get("isActive"));

            refreshEmployeeTable();
        });
    }

    private void refreshEmployeeTable() {
        employeeData.setAll(database.getAllEmployees());
    }

    private void addSampleData() {
        // Add sample employees
        database.addEmployee(new Employee<>(UUID.randomUUID(), true, 15,
                78000.0, 4.2, "IT", "John James"));

            // Add sample employees
            database.addEmployee(new Employee<>(UUID.randomUUID(), true, 15,
                    78000.0, 4.2, "IT", "John James"));

            database.addEmployee(new Employee<>(UUID.randomUUID(), true, 5,
                    65000.0, 3.8, "HR", "Sarah Johnson"));

            database.addEmployee(new Employee<>(UUID.randomUUID(), true, 12,
                    92000.0, 4.5, "Finance", "Michael Brown"));

            database.addEmployee(new Employee<>(UUID.randomUUID(), true, 9,
                    82000.0, 4.7, "IT", "Emily Wilson"));

            database.addEmployee(new Employee<>(UUID.randomUUID(), true, 4,
                    68000.0, 3.5, "Marketing", "Robert Smith"));

            database.addEmployee(new Employee<>(UUID.randomUUID(), true, 15,
                    105000.0, 4.9, "Finance", "Jennifer Lee"));

            database.addEmployee(new Employee<>(UUID.randomUUID(), false, 2,
                    58000.0, 3.2, "HR", "David Taylor"));

            database.addEmployee(new Employee<>(UUID.randomUUID(), true, 7,
                    89000.0, 4.3, "IT", "Jessica Martinez"));

            database.addEmployee(new Employee<>(UUID.randomUUID(), true, 6,
                    72000.0, 4.0, "Marketing", "Andrew Wilson"));

            database.addEmployee(new Employee<>(UUID.randomUUID(), true, 10,
                    98000.0, 4.6, "Finance", "Sophia Chen"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}