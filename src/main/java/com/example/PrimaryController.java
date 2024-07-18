package com.example;

import java.io.File;
import java.io.IOException;

import com.example.data.LineItem;
import com.example.data.WriteData;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class PrimaryController {

     @FXML
    private TableColumn<LineItem, String> budgetCol_Actual;

    @FXML
    private TableColumn<LineItem, Double> budgetCol_Budget;

    @FXML
    private TableColumn<LineItem, Double> budgetCol_Category;

    @FXML
    private TableColumn<LineItem, Double> budgetCol_Diff;

    @FXML
    private VBox categoryBox;

    @FXML
    private AnchorPane myAnchorPane;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private TableView<LineItem> tableView_Budget;

    @FXML
    void readActual(ActionEvent event) {
        // routine to open dialog box to select file
        String startFile = "";

        startFile = "D:\\VSCwork\\budget\\";

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(startFile));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // Process the selected file
            String filePath = selectedFile.getAbsolutePath();
            System.out.println("File Path: " + filePath);
            

          }

    }

    @FXML
    void readHeadings(ActionEvent event) {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        int temp = WriteData.getRejectedRecordCount();
        // rejectedRecords.setText(String.valueOf(temp));
        // addedRecords.setText(String.valueOf(WriteData.getAddedRecordCount()));

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
