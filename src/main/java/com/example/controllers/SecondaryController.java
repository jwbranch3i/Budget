package com.example.controllers;

import java.util.HashMap;
import java.util.Map;

import com.example.data.Categories;
import com.example.data.ReadData;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class SecondaryController {
  @FXML
  private TableColumn<Categories, String> catColumnCategory;

  @FXML
  private TableColumn<Categories, String> catColumnParent;

  @FXML
  private TableColumn<Categories, Integer> catColumnType;

  @FXML
  private TableView<Categories> catTable;

  // Define the map to convert Integer to String
  private final Map<Integer, String> typeMap = new HashMap<>();
  private final Map<String, Integer> reverseTypeMap = new HashMap<>();

  public void initialize() {
    // Populate the map with Integer to String mappings
    typeMap.put(0, "Income");
    typeMap.put(1, "Mandatory");
    typeMap.put(2, "Discretionary");

    // Populate the reverse map with String to Integer mappings
    reverseTypeMap.put("Income", 0);
    reverseTypeMap.put("Mandatory", 1);
    reverseTypeMap.put("Discretionary", 2);

    catColumnType.setCellValueFactory(new PropertyValueFactory<>("comboBox"));

    catColumnParent.setCellValueFactory(new PropertyValueFactory<Categories, String>("Parent"));
    catColumnParent.setCellFactory(TextFieldTableCell.forTableColumn());

    catColumnCategory.setCellValueFactory(new PropertyValueFactory<Categories, String>("Category"));
    catColumnCategory.setCellFactory(TextFieldTableCell.forTableColumn());

    // Read categories from the database into edit categories tableview
    catTable.setItems(FXCollections.observableArrayList(ReadData.getCategories()));

  }
}