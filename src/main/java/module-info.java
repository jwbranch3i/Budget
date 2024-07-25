module com.example {

    requires transitive javafx.controls;
    requires transitive java.sql;
    requires transitive javafx.base;
    

    requires transitive javafx.fxml;
    requires com.opencsv;
    requires javafx.graphics;

    opens com.example to javafx.fxml;

    exports com.example;
    exports com.example.data;

}
