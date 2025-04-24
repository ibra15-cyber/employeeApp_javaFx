module com.ibra.employeeapplication {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.naming;
    requires java.logging;
    requires junit;


    opens com.ibra.employeeapplication to javafx.fxml;
    exports com.ibra.employeeapplication;
    exports com.ibra.employeeapplication.backend.test; // Export the test package for JUnit

}