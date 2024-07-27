package com.example;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import com.example.data.DB;
import com.example.data.LineItem;
import com.example.data.LineItemCSV;
import com.example.data.ReadData;
import com.example.data.WriteData;
import com.opencsv.CSVReader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.converter.DoubleStringConverter;

public class PrimaryController {

 
    @FXML
    private VBox categoryBox;

    @FXML
    private TableColumn<LineItem, Double> discretionaryTable_Actual;

    @FXML
    private TableColumn<LineItem, Double> discretionaryTable_Budget;

    @FXML
    private TableColumn<LineItem, String> discretionaryTable_Category;

    @FXML
    private TableColumn<LineItem, Double> discretionaryTable_Diff;

    @FXML
    private TableColumn<LineItem, Double> incomeTable_Actual;

    @FXML
    private TableColumn<LineItem, Double> incomeTable_Budget;

    @FXML
    private TableColumn<LineItem, String> incomeTable_Category;

    @FXML
    private TableColumn<LineItem, Double> incomeTable_Diff;

    @FXML
    private TableColumn<LineItem, Double> mandatoryTable_Actual;

    @FXML
    private TableColumn<LineItem, Double> mandatoryTable_Budget;

    @FXML
    private TableColumn<LineItem, String> mandatoryTable_Category;

    @FXML
    private TableColumn<LineItem, Double> mandatoryTable_Diff;

    @FXML
    private AnchorPane myAnchorPane;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private TableView<LineItem> tableView_Discretionary;

    @FXML
    private TableView<LineItem> tableView_Income;

    @FXML
    private TableView<LineItem> tableView_Mandatory;


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
            getActuals();

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

        incomeTable_Category.setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
        incomeTable_Category.setCellFactory(TextFieldTableCell.forTableColumn());
        incomeTable_Category.setOnEditCommit(e -> incomeTableCategory_OnEditCommit(e));

        incomeTable_Actual.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
        incomeTable_Actual.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        incomeTable_Actual.setOnEditCommit(e -> incomeTableActual_OnEditCommit(e));

        incomeTable_Budget.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
        incomeTable_Budget.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        incomeTable_Budget.setOnEditCommit(e -> incomeTableActual_OnEditCommit(e));

        incomeTable_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
        incomeTable_Diff.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        incomeTable_Diff.setOnEditCommit(e -> incomeTableActual_OnEditCommit(e));
    }

    public static ArrayList<LineItemCSV> readActual(String file, LocalDate inDate) {

        ArrayList<LineItemCSV> items = new ArrayList<LineItemCSV>();

        LineItemCSV newLineItem = new LineItemCSV();
        LineItemCSV existingCategory = new LineItemCSV();
        LineItemCSV existingActual = new LineItemCSV();
        int existingCategoryId = 0;

        String[] nextRecord;
        String category = "";
        String parent = "";
        String workingType = "";

        Double amount = 0.0;

        int leadingSpaces = 0;
        int type = 0;
        int newRecordType = 0;

        try {
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReader(filereader);

            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {

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
                        existingCategory = ReadData.categoryFindRecord(newLineItem);

                        if ((existingCategory.getId() == -1)) {
                            existingCategory = WriteData.categoryInsertRecord(newLineItem);
                        }

                        existingActual = ReadData.actualFindCategory(existingCategory);

                        if (existingActual.getId() == -1) {
                            WriteData.actualInsertRecord(existingActual, existingCategory);
                        } else {
                            WriteData.autualUpdateAmount(existingActual);
                        }

                        break;

                    case 8:
                        parent = workingType;
                        newLineItem = new LineItemCSV(type, inDate, parent, category, amount);
                        existingCategory = ReadData.categoryFindRecord(newLineItem);

                        if ((existingCategory.getId() == -1)) {
                            existingCategory = WriteData.categoryInsertRecord(newLineItem);
                        }

                        existingActual = ReadData.actualFindCategory(existingCategory);

                        if (existingActual.getId() == -1) {
                            WriteData.actualInsertRecord(existingActual, existingCategory);
                        } else {
                            WriteData.autualUpdateAmount(existingActual);
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

    public void getActuals() {
        Task<ObservableList<LineItem>> task = new ReadActualAmount();
        tableView_Income.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
    }

    public void incomeTableCategory_OnEditCommit(TableColumn.CellEditEvent<LineItem, String> e) {
        LineItem item = e.getRowValue();
        item.setCategory(e.getNewValue());
    }

    public void incomeTableActual_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
        LineItem item = e.getRowValue();
        item.setActual(e.getNewValue());
    }

    public void incomeTableBudget_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
        LineItem item = e.getRowValue();
        item.setBudget(e.getNewValue());
    }

    public void incomeTableDiff_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
        LineItem item = e.getRowValue();
        item.setDiff(e.getNewValue());
    }

}

class ReadActualAmount extends Task<ObservableList<LineItem>> {
    public ObservableList<LineItem> call() {
        return FXCollections.observableArrayList(ReadData.getTableAmounts());
    }
}