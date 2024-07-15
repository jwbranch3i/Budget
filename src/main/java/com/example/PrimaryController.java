package com.example;

import java.io.IOException;

import com.example.data.DB;
import com.example.data.LineHeadings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PrimaryController {

    @FXML
    private Button primaryButton;

    @FXML
    void readHeadings(ActionEvent event) {
        LineHeadings.readHeadings(DB.CSV_FILE);


    }

    @FXML
    private void switchToSecondary() throws IOException {
        // App.setRoot("secondary");
    }
}
