package com.example.controllers;

import java.time.format.DateTimeFormatter;

import com.example.data.LineItem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EditItemController {
    @FXML
    private Label Category_LBL;

    @FXML
    private Label actual_LBL;

    @FXML
    private Button btn_Cancel;

    @FXML
    private Button btn_Save;

    @FXML
    private Label budget_LBL;

    @FXML
    private Label computed_LBL;

    @FXML
    private Label date_LBL;

    @FXML
    private TextField startBal_Field;

    LineItem item;



    public void setItem(LineItem editItem) {
        this.item = editItem;
        Category_LBL.setText(item.getCategory());
        actual_LBL.setText(item.getActual().toString());
        budget_LBL.setText(item.getBudget().toString());
        computed_LBL.setText(item.getComputed().toString());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String formattedDate = item.getDate().format(formatter);
        date_LBL.setText(formattedDate);
        
        startBal_Field.setText(item.getStartBal().toString());
    }

    public void initialize() {
    }

}
