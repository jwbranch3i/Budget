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

  public void initialize() {
    // Populate the map with Integer to String mappings
    typeMap.put(0, "Income");
    typeMap.put(1, "Mandatory");
    typeMap.put(2, "Discretionary");

    catColumnType.setCellValueFactory(new PropertyValueFactory<Categories, Integer>("Type"));
    catColumnType.setCellFactory(column -> new TableCell<Categories, Integer>() {

      private final ComboBox<String> comboBox = new ComboBox<>();

      @Override
      protected void updateItem(Integer items, boolean empty) {
        super.updateItem(items, empty);

        if (empty || items == null) {
          setGraphic(null);
        }
        else {
          // Convert the Integer value to a list of strings
          List<String> stringList = convertIntegerToList();
          comboBox.setItems(FXCollections.observableArrayList(stringList));

          // Set the ComboBox value to the current item in the row
          Categories currentCategory = getTableView().getItems().get(getIndex());
          comboBox.getSelectionModel().select(currentCategory.getType());

          // Add listener to save changes to the database
          comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
              if (newValue != null) {
                // Update the category type
                if (currentCategory.getType() != getKeyByValue(typeMap, newValue)) {
                  currentCategory.setType(getKeyByValue(typeMap, newValue));
                  System.out.println(
                      "Category " + oldValue + " type updated to: " + newValue + "**" + currentCategory.toString());
                }
              }
            }
          });
          setGraphic(comboBox);
        }
      }

      // Helper method to convert Integer to List<String>
      private List<String> convertIntegerToList() {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < typeMap.size(); i++) {
          stringList.add(typeMap.get(i));
        }
        return stringList;
      }

      // Helper method to get key by value from the map
      private Integer getKeyByValue(Map<Integer, String> map, String value) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
          if (entry.getValue().equals(value)) {
            return entry.getKey();
          }
        }
        return null;
      }
    });

    catColumnParent.setCellValueFactory(new PropertyValueFactory<Categories, String>("Parent"));
    catColumnParent.setCellFactory(TextFieldTableCell.forTableColumn());

    catColumnCategory.setCellValueFactory(new PropertyValueFactory<Categories, String>("Category"));
    catColumnCategory.setCellFactory(TextFieldTableCell.forTableColumn());

    // Read categories from the database into edit categories tableview
    catTable.setItems(FXCollections.observableArrayList(ReadData.getCategories()));

  }
}