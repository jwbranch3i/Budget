package com.example.controllers;

import com.example.Util;
import com.example.data.LineItem;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PrimaryControllerExtend {

    private TableView<LineItem> tableView_Total;
    private TableColumn<LineItem, String> totalTable_Category;
    private TableColumn<LineItem, Double> totalTable_Actual;
    private TableColumn<LineItem, Double> totalTable_Budget;
    private TableColumn<LineItem, Double> totalTable_Diff;

    public PrimaryControllerExtend(TableView<LineItem> tableView_Total,
            TableColumn<LineItem, String> totalTable_Category, TableColumn<LineItem, Double> totalTable_Actual,
            TableColumn<LineItem, Double> totalTable_Budget, TableColumn<LineItem, Double> totalTable_Diff) {
        this.tableView_Total = tableView_Total;
        this.totalTable_Category = totalTable_Category;
        this.totalTable_Actual = totalTable_Actual;
        this.totalTable_Budget = totalTable_Budget;
        this.totalTable_Diff = totalTable_Diff;

        initialize();
    }

    private void initialize() {

        totalTable_Category.setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));

        totalTable_Actual.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("Actual"));
        totalTable_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

        totalTable_Budget.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("Budget"));
        totalTable_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

        totalTable_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("Diff"));
        totalTable_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

    }

}
