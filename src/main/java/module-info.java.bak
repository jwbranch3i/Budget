module com.example {

    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;

    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive java.sql;
    requires org.slf4j;

    opens com.budget to javafx.fxml;
    opens com.budget.controllers to javafx.fxml;
    opens com.budget.dataModal to javafx.base;

    exports com.budget;
    exports com.budget.controllers;
    exports com.budget.dataModal;
}

