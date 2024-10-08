package com.example.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.example.data.Categories;
import com.example.data.ReadData;
import com.example.data.WriteData;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

public class SecondaryController {
  @FXML
  private TableColumn<Categories, Boolean> catColumnHide;

  @FXML
  private TableColumn<Categories, String> catColumnCategory;

  @FXML
  private TableColumn<Categories, String> catColumnParent;

  @FXML
  private TableColumn<Categories, Integer> catColumnType;

  @FXML
  private TableView<Categories> catTable;

  @FXML
  private Button btn_finishEdit;

  @FXML
  void button_finishEdit(ActionEvent event) {

    // Close the window
    Stage stage = (Stage) btn_finishEdit.getScene().getWindow();
    stage.close();
  }

  // Define the map to convert Integer to String
  private final Map<Integer, String> typeMap = new HashMap<>();

  public void initialize() {

    // Get the stage from the current scene
    Platform.runLater(() -> {
      Stage stage = (Stage) btn_finishEdit.getScene().getWindow();
      stage.setOnCloseRequest(event -> {
        // Call the button_finishEdit method
        button_finishEdit(new ActionEvent());
      });
    });

    // Populate the map with Integer to String mappings
    typeMap.put(0, "Income");
    typeMap.put(1, "Mandatory");
    typeMap.put(2, "Discretionary");

    /****************************
     * column setup
     **********************************************/
    catColumnHide.setCellValueFactory(new PropertyValueFactory<Categories, Boolean>("Hide"));
    catColumnHide.setCellFactory(column -> new TableCell<Categories, Boolean>() {
      @Override
      protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
          setGraphic(null);
        }
        else {
          // Create a new CheckBox instance for each row
          CheckBox isHidden = new CheckBox();
          isHidden.setSelected(item);

          // Set the CheckBox value to the current item in the row
          Categories currentCategory = getTableView().getItems().get(getIndex());
          isHidden.setSelected(currentCategory.getHide());

          // Add listener to save changes to the database
          isHidden.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
              // Update the category hide status
              if (currentCategory.getHide() != newValue) {
                currentCategory.setHide(newValue);
                WriteData.categoryUpdateHide(currentCategory);
              }
            }
          });
          setGraphic(isHidden);
          setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
          setAlignment(Pos.CENTER); // Center the CheckBox

        }
      }
    });

    catColumnType.setCellValueFactory(new PropertyValueFactory<Categories, Integer>("Type"));
    catColumnType.setCellFactory(column -> new TableCell<Categories, Integer>() {

      @Override
      protected void updateItem(Integer items, boolean empty) {
        super.updateItem(items, empty);

        if (empty || items == null) {
          setGraphic(null);
        }
        else {
          // Create a new ComboBox instance for each row
          ComboBox<String> comboBox = new ComboBox<>();

          // Convert the Integer value to a list of strings
          List<String> stringList = convertIntegerToList();
          comboBox.setItems(FXCollections.observableArrayList(stringList));

          // Set the ComboBox value to the current item in the row
          Categories currentCategory = getTableView().getItems().get(getIndex());
          comboBox.getSelectionModel().select(currentCategory.getType());

          // Add listener to save changes to the database
          comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
              // Update the category type
              if (currentCategory.getType() != getKeyByValue(typeMap, newValue)) {
                currentCategory.setType(getKeyByValue(typeMap, newValue));
                WriteData.categoryUpdateType(currentCategory);
              }
            }
          });
          setGraphic(comboBox);
        }

      }

      // // Helper method to convert Integer to List<String>
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
   // catColumnType.setOnEditCommit(e -> catColumnType_OnEditCommit(e));

    catColumnParent.setCellValueFactory(new PropertyValueFactory<Categories, String>("Parent"));
    catColumnParent.setCellFactory(TextFieldTableCell.forTableColumn());

    catColumnCategory.setCellValueFactory(new PropertyValueFactory<Categories, String>("Category"));
    catColumnCategory.setCellFactory(TextFieldTableCell.forTableColumn());

    // Read categories from the database into edit categories tableview
    catTable.setItems(FXCollections.observableArrayList(ReadData.getCategories()));

  }

  // public void catColumnType_OnEditCommit(TableColumn.CellEditEvent<Categories, Integer> e) {
  //   Categories category = e.getRowValue();
  //   category.setType(e.getNewValue());
  //   WriteData.categoryUpdateType(category);
  // }
}