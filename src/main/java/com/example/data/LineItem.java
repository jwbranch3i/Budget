package com.example.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LineItem {
    int id = 0;
    int type = 0;
    private SimpleStringProperty parent = new SimpleStringProperty("");
    private SimpleStringProperty category = new SimpleStringProperty("");

    public LineItem() {
    }

    public LineItem(SimpleStringProperty type, SimpleStringProperty parent, SimpleStringProperty category) {
        this.type = type;
        this.parent = parent;
        this.category = category;
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

    /********************* category ***********************************/
    public StringProperty parentProperty() {
        return this.parent;
    }

    public String getParent() {
        return this.parent.get();
    }

    public void setParent(String parent) {
        this.parent.set(parent);
    }

    /******************** parent ************************************/
    public StringProperty categoryProperty() {
        return this.category;
    }

    public String getCategory() {
        return this.category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", type='" + getType() + "'" +
            ", parent='" + getParent() + "'" +
            ", category='" + getCategory() + "'" +
            "}";
    }


}