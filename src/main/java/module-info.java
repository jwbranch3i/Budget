module com.example {

    requires transitive javafx.controls;

    requires transitive javafx.fxml;
    requires com.opencsv;



    opens com.example to javafx.fxml;

    exports com.example;

}
