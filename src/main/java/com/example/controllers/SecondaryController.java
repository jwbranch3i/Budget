package com.example.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.example.data.Categories;
import com.example.data.ReadData;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
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

   catColumnType.setCellValueFactory(new PropertyValueFactory<>("Type"));
   catColumnType.setCellFactory(reverseTypeMap.get(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList("Income", "Mandatory", "Discretionary"))));

   catColumnType.setOnEditCommit(event -> {
       Categories category = event.getRowValue();
       String newValue = event.getNewValue();
       category.setType(reverseTypeMap.get(newValue));
       saveCategoryToDatabase(category);
   });

  // Helper method to convert Integer to List<String>
  // private List<String> convertIntegerToList() {
  //   List<String> stringList = new ArrayList<>();
  //   for (int i = 0; i < typeMap.size(); i++) {
  //     stringList.add(typeMap.get(i));
  //   }
  //   return stringList;
  // }

  // Helper method to get key by value from the map
  // private Integer getKeyByValue(Map<Integer, String> map, String value) {
  //   for (Map.Entry<Integer, String> entry : map.entrySet()) {
  //     if (entry.getValue().equals(value)) {
  //       return entry.getKey();
  //     }
  //   }
  //   return null;
  // }


catColumnParent.setCellValueFactory(new PropertyValueFactory<Categories,String>("Parent"))
catColumnParent.setCellFactory(TextFieldTableCell.forTableColumn());

catColumnCategory.setCellValueFactory(new PropertyValueFactory<Categories,String>("Category"));
catColumnCategory.setCellFactory(TextFieldTableCell.forTableColumn());

}