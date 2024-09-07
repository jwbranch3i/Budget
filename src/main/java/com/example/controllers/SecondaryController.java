package com.example.controllers;

import java.util.ArrayList;
import java.util.List;

import com.example.data.Categories;
import com.example.data.ReadData;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
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

  public void initialize() {

    catColumnType.setCellValueFactory(new PropertyValueFactory<Categories, Integer>("Type"));
    catColumnType.setCellFactory(column -> new TableCell<Categories, Integer>() {
      // private final ComboBox<String> comboBox = new ComboBox<>();

      // @Override
      // protected void updateItem(Integer items, boolean empty) {
      // super.updateItem(items, empty);

      // if (empty || items == null) {
      // setGraphic(null);
      // }
      // else {
      // // Convert the Integer value to a list of strings
      // List<String> stringList = convertIntegerToList(items);

      // // Set the ComboBox value to the current item
      // comboBox.setValue(stringList.get(0)); // Assuming the first item is the
      // default value

      // comboBox.setItems(FXCollections.observableArrayList(stringList));
      // setGraphic(comboBox);
      // }
      // }

      // // Helper method to convert Integer to List<String>
      // private List<String> convertIntegerToList(Integer items) {
      // // Example conversion logic
      // List<String> stringList = new ArrayList<>();
      // stringList.addAll(FXCollections.observableArrayList("Income",
      // "Mandatory", "Discretionary"));
      // return stringList;
      // }
      // });

      private final ComboBox<String> comboBox = new ComboBox<>();

      @Override
      protected void updateItem(Integer items, boolean empty) {
        super.updateItem(items, empty);

        if (empty || items == null) {
          setGraphic(null);
        }
        else {
          // Convert the Integer value to a list of strings
          List<String> stringList = convertIntegerToList(items);
          comboBox.setItems(FXCollections.observableArrayList(stringList));

          // Set the ComboBox value to the current item in the row
          Categories currentCategory = getTableView().getItems().get(getIndex());
          comboBox.getSelectionModel().select(currentCategory.getType());
          setGraphic(comboBox);
        }
      }

      // Helper method to convert Integer to List<String>
      private List<String> convertIntegerToList(Integer items) {
        // Example conversion logic
        List<String> stringList = new ArrayList<>();
        stringList.addAll(FXCollections.observableArrayList("Income", "Mandatory", "Discretionary"));
        return stringList;
      }
    });

    catColumnParent.setCellValueFactory(new PropertyValueFactory<Categories, String>("Parent"));
    catColumnParent.setCellFactory(TextFieldTableCell.forTableColumn());

    catColumnCategory.setCellValueFactory(new PropertyValueFactory<Categories, String>("Category"));
    catColumnCategory.setCellFactory(TextFieldTableCell.forTableColumn());

    catTable.setItems(FXCollections.observableArrayList(ReadData.getCategories()));

    System.out.println("Categories loaded - " + catTable.getItems().size() + " items");

    // Task<Void> task = new Task<Void>() {
    // @Override
    // protected Void call() throws Exception {
    // catTable.setItems(FXCollections.observableArrayList(ReadData.getCategories()));

    // System.out.println("Categories loaded - " + catTable.getItems().size() +
    // " items");
    // return null;
    // }
    // };
    // new Thread(task).start();

  }
}