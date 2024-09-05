package com.example.controllers;

import java.util.List;
import java.util.ArrayList; // Add this import statement

import com.example.data.Categories;
import com.example.data.ReadData;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
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
  private TableColumn<Categories, List<String>> catColumnType;

  @FXML
  private TableView<Categories> catTable;

  public void initialize() {

    catColumnType.setCellValueFactory(new PropertyValueFactory<Categories, List<String>>("Type"));
    catColumnType.setCellFactory(column -> new TableCell<Categories, List<String>>() {

      private final ListView<String> listView = new ListView<String>();

      @Override
      protected void updateItem(List<String> items, boolean empty) {
        super.updateItem(items, empty);

        if (empty || items == null) {
          setGraphic(null);
        }
        else {
          listView.setItems(FXCollections.observableArrayList(items));
          setGraphic(listView);
        }
      }
    });

    catColumnParent.setCellValueFactory(new PropertyValueFactory<Categories, String>("Parent"));
    catColumnParent.setCellFactory(TextFieldTableCell.forTableColumn());

    catColumnCategory.setCellValueFactory(new PropertyValueFactory<Categories, String>("Category"));
    catColumnCategory.setCellFactory(TextFieldTableCell.forTableColumn());

    Task<ArrayList<Categories>> task = new Task<ArrayList<Categories>>() {
      @Override
      protected ArrayList<Categories> call() throws Exception {
        return ReadData.getCategories();
      }
    };
    new Thread(task).start();

    catTable.setItems(FXCollections.observableArrayList(ReadData.getCategories()));
  }
}