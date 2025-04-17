module com.ibra.employeeapplication {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.ibra.employeeapplication to javafx.fxml;
    exports com.ibra.employeeapplication;
}