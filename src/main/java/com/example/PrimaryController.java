package com.example;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

import com.example.data.DB;
import com.example.data.LineItem;
import com.example.data.LineItemCSV;
import com.example.data.ReadData;
import com.example.data.WriteData;
import com.opencsv.CSVReader;

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
            readActual(filePath, LocalDate.now());

        }
    }

    @FXML
    void readHeadings(ActionEvent event) {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

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

    public static ArrayList<LineItemCSV> readActual(String file, LocalDate inDate) {

        ArrayList<LineItemCSV> items = new ArrayList<LineItemCSV>();

        LineItemCSV newLineItem = new LineItemCSV();
        LineItemCSV existingCategory = new LineItemCSV();
        int existingCategoryId = 0;

        String[] nextRecord;
        String category = "";
        String parent = "";
        String workingType = "";

        Double amount = 0.0;

        int leadingSpaces = 0;
        int type = 0;
        int newRecordType = 0;

        int lineCount = 0;

        try {
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReader(filereader);

            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                // for debugging
                lineCount++;

                // if the line is empty, skip it
                if (nextRecord.length <= 1) {
                    continue;
                }

                leadingSpaces = nextRecord[1].length() - nextRecord[1].trim().length();
                if (nextRecord[1].trim().equals("INFLOWS")) {
                    type = DB.INFLOW;
                    continue;
                } else if (nextRecord[1].trim().equals("OUTFLOWS")) {
                    type = DB.OUTFLOW;
                    continue;
                }

                category = nextRecord[1].trim();
                // if category contains the string 'TOTAL' then skip it
                if (category.contains("TOTAL")) {
                    continue;
                }

                if (nextRecord.length < 3) {
                    continue;
                }

                try {
                    amount = Double.parseDouble(nextRecord[2].replaceAll(",", ""));
                } catch (NumberFormatException e) {
                    amount = 0.0;
                }

                switch (leadingSpaces) {
                    case 4: // if the line is a category

                        newRecordType = type;
                        parent = "";
                        workingType = category;
                        newLineItem = new LineItemCSV(newRecordType, inDate, parent, category, amount);
                        existingCategoryId = ReadData.categoryFindRecord(newLineItem);

                        if ((existingCategoryId == -1)) {
                            existingCategory = WriteData.categoryInsertRecord(newLineItem);
                        }

                        int existingActualId = ReadData.actualFindCategory(existingCategory);

                        if (existingActualId == -1) {
                            WriteData.actualInsertRecord(existingCategory);
                        } else {
                            WriteData.autualUpdateAmount(existingActualId, existingCategory.getAmount());
                        }

                        break;

                    case 8:
                        parent = workingType;
                        newLineItem = new LineItemCSV(type, inDate, parent, category, amount);
                        existingCategoryId = ReadData.categoryFindRecord(newLineItem);

                        if ((existingCategoryId == -1)) {
                            existingCategory = WriteData.categoryInsertRecord(newLineItem);
                        }

                        existingActualId = ReadData.actualFindCategory(existingCategory);

                        if (existingActualId == -1) {
                            WriteData.actualInsertRecord(existingCategory);
                        } else {
                            WriteData.autualUpdateAmount(existingActualId, existingCategory.getAmount());
                        }

                        break;

                    default:
                        // Handle any other number of leading spaces
                        break;
                }

            }
            csvReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

}
