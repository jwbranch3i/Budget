package com.budget.controllers;

import com.budget.dataModal.LineItem;
import com.budget.dataModal.WriteData;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class EditItemController {
    @FXML
    private GridPane gridPane;

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
    private CheckBox chkbox_include;

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
        chkbox_include.setSelected(item.include_in_total());
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
