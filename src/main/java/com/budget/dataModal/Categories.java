package com.budget.dataModal;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.List; // Add this import statement
import java.util.ArrayList; // Add this import statement

public class Categories {
    int id = 0;
    private SimpleBooleanProperty hide = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty in_total = new SimpleBooleanProperty(true);
    private SimpleIntegerProperty type = new SimpleIntegerProperty(0);
    private SimpleStringProperty parent = new SimpleStringProperty("");
    private SimpleStringProperty category = new SimpleStringProperty("");
    private SimpleIntegerProperty acct = new SimpleIntegerProperty(0);

    private static List<String> items = new ArrayList<String>();

    public Categories() {
    }

    public Categories(Integer type, String parent, String category) {
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

    /********************* in_total ***********************************/
    public SimpleBooleanProperty in_totalProperty() {
        return this.in_total;
    }

    public Boolean in_total() {
        return in_total.get();
    }
    public Boolean getIn_total() {
        return in_total.get();
    }
    public void in_total(boolean in_total) {
        this.in_total.set(in_total);
    }

    /********************* hide ***********************************/
    public SimpleBooleanProperty hideProperty() {
        return this.hide;
    }

    public Boolean hide() {
        return hide.get();
    }
    public Boolean getHide() {
        return hide.get();
    }

    public void hide(boolean hide) {
        this.hide.set(hide);
    }

    /********************* type ***********************************/
    public int getType() {
        return this.type.get();
    }

    public void setType(int type) {
        this.type.set(type);
    }

    /********************* acct ***********************************/
    public int getAcct() {
        return this.acct.get();
    }

    public void setAcct(int acct) {
        this.acct.set(acct);
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
            ", hide='" + hide() + "'" +
            ", in_total='" + in_total() + "'" +
            ", type='" + getType() + "'" +
            ", parent='" + getParent() + "'" +
            ", category='" + getCategory() + "'" +
            ", acct='" + getAcct() + "'" +
            "}";
    }
   

}
