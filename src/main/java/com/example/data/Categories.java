package com.example.data;

import javafx.beans.property.SimpleStringProperty;
import java.util.List; // Add this import statement
import java.util.ArrayList; // Add this import statement

public class Categories {
    int id = 0;
    Boolean show = true;
    int type = 0;
    private SimpleStringProperty parent = new SimpleStringProperty("");
    private SimpleStringProperty category = new SimpleStringProperty("");
    private static List<String> items = new ArrayList<String>();

    public Categories() {
    }

    public Categories(int type, String parent, String category){
        this.type = type;
        this.parent.set(parent);
        this.category.set(category);

        items.add("Income");
        items.add("Manditory");
        items.add("Discretionary");
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

    public String isShow() {
        return show.toString();
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", show='" + isShow() + "'" +
            ", type='" + getType() + "'" +
            ", parent='" + getParent() + "'" +
            ", category='" + getCategory() + "'" +
            ", items='" + getItems() + "'" +
            "}";
    }

}
