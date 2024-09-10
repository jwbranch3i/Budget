package com.example.data;

import java.util.HashMap;
import java.util.List; // Add this import statement
import java.util.Map;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class Categories {
    int id = 0;
    Boolean show = true;
    int type = 0;
    private SimpleStringProperty parent = new SimpleStringProperty("");
    private SimpleStringProperty category = new SimpleStringProperty("");
    // private static List<String> items = new ArrayList<String>();
    private static ObservableList<String> items = FXCollections.observableArrayList("Income", "Mandatory",
            "Discretionary");
    private ComboBox<String> comboBox;
    private SimpleObjectProperty<ComboBox<String>> comboBoxProperty = new SimpleObjectProperty<ComboBox<String>>();
    private final Map<Integer, String> typeMap = new HashMap<>();

    public Categories() {
        typeMap.put(0, "Income");
        typeMap.put(1, "Mandatory");
        typeMap.put(2, "Discretionary");

        comboBox = new ComboBox<String>(items);

        int temp = getTableView().getItems().get(getIndex())
        comboBox.setValue(typeMap.get(type));

        comboBoxProperty.set(comboBox);

    }

    public Categories(int type, String parent, String category) {

        typeMap.put(0, "Income");
        typeMap.put(1, "Mandatory");
        typeMap.put(2, "Discretionary");

        comboBox = new ComboBox<String>(items);
        comboBox.setValue(typeMap.get(type));

        comboBoxProperty.set(comboBox);
    }

    /********************** id **********************************/
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /********************* type ***********************************/
    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /********************* parent ***********************************/
    public SimpleStringProperty getParentProperty() {
        return this.parent;
    }

    public String getParent() {
        return parent.get();
    }

    public void setParent(String newParent) {
        this.parent.set(newParent);
    }

    /********************* category ***********************************/
    public SimpleStringProperty getCategoryProperty() {
        return this.category;
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String newCategory) {
        this.category.set(newCategory);
    }

    /********************* items ***********************************/
    public static List<String> getItems() {
        return items;
    }

    /********************* comboBox ***********************************/
    public ComboBox<String> getComboBox() {
        return comboBox;
    }

    public SimpleObjectProperty<ComboBox<String>> getComboBoxProperty() {
        return comboBoxProperty;
    }

    /********************* show ***********************************/
    public String isShow() {
        return show.toString();
    }

    @Override
    public String toString() {
        return "{" + " id='" + getId() + "'" + ", show='" + isShow() + "'" + ", type='" + getType() + "'" + ", parent='"
                + getParent() + "'" + ", category='" + getCategory() + "'" + ", items='" + getItems() + "'" + "}";
    }

}
