package com.example.data;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.List; // Add this import statement
import java.util.ArrayList; // Add this import statement

public class Categories {
    int id = 0;
  private final SimpleBooleanProperty hide = new SimpleBooleanProperty(false);
    private SimpleIntegerProperty type = new SimpleIntegerProperty(0);
    private SimpleStringProperty parent = new SimpleStringProperty("");
    private SimpleStringProperty category = new SimpleStringProperty("");
    private static List<String> items = new ArrayList<String>();

    public Categories() {
    }

    public Categories(Integer type, String parent, String category){
        this.type.set(type);
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

    /********************* hide ***********************************/
    public SimpleBooleanProperty hideProperty() {
        return this.hide;
    }

    public Boolean getHide() {
        return hide.get();
    }

    public void setHide(boolean newHide) {
        this.hide.set(newHide);
    }

    /********************* type ***********************************/
    public int getType() {
        return this.type.get();
    }

    public void setType(int type) {
        this.type.set(type);
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




    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", show='" + getHide().toString() + "'" +
            ", type='" + getType() + "'" +
            ", parent='" + getParent() + "'" +
            ", category='" + getCategory() + "'" +
            ", items='" + getItems() + "'" +
            "}";
    }

}
