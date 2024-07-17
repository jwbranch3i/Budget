package com.example;

import java.io.IOException;

import com.example.data.DB;
import com.example.data.WriteData;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class PrimaryController {

     @FXML
    private Label addedRecords;

    @FXML
    private VBox categoryBox;

    @FXML
    private CheckBox clearCat;

    @FXML
    private AnchorPane myAnchorPane;

    @FXML
    private Button primaryButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label rejectedRecords;

    @FXML
    void readHeadings(ActionEvent event) {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        WriteData.readHeadings(DB.CSV_FILE, clearCat.isSelected());
        int temp = WriteData.getRejectedRecordCount();
        rejectedRecords.setText(String.valueOf(temp));
        addedRecords.setText(String.valueOf(WriteData.getAddedRecordCount()));

        progressIndicator.setVisible(false);

    }

    @FXML
    private void switchToSecondary() throws IOException {
        // App.setRoot("secondary");
    }

    public void initialize() {
        myAnchorPane.getStyleClass().add("catBox");
    }

}
