package com.example.controllers;

import com.example.data.LineItem;
import com.example.data.WriteData;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EditItemController {
    @FXML
    private Label id_LBL;

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

    @FXML
    void button_cancel(ActionEvent event) {
        btn_Cancel.getScene().getWindow().hide();
    }

    @FXML
    void button_saveStartBal(ActionEvent event) {
        item.setStartBal(Double.parseDouble(startBal_Field.getText()));
        WriteData.actualUpdate(item);
        btn_Save.getScene().getWindow().hide();

    }

    public void setItem(LineItem editItem) {
        this.item = editItem;
        id_LBL.setText(String.valueOf(item.getId()));
        Category_LBL.setText(item.getCategory());
        actual_LBL.setText(item.getActual().toString());
        budget_LBL.setText(item.getBudget().toString());
        computed_LBL.setText(item.getComputed().toString());
        date_LBL.setText(editItem.getDate().toString());
        startBal_Field.setText(item.getStartBal().toString());
    }

    public void initialize() {
    }

}
