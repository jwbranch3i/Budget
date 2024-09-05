module com.example {

    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;

    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive java.sql;

    opens com.example to javafx.fxml;
    opens com.example.controllers to javafx.fxml;
    opens com.example.data to javafx.base;

    exports com.example;
    exports com.example.controllers;
    exports com.example.data;
}

