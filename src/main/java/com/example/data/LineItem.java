package com.example.data;

import java.time.LocalDate;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LineItem {
    int id = 0;
    Boolean hide = false;
    int type = 0;
    Boolean isCategory = false;
    private SimpleObjectProperty<LocalDate> date = new SimpleObjectProperty<LocalDate>(LocalDate.now());
    private SimpleStringProperty parent = new SimpleStringProperty("");
    private SimpleStringProperty category = new SimpleStringProperty("");
    private SimpleDoubleProperty actual = new SimpleDoubleProperty(0.0);
    private SimpleDoubleProperty budget = new SimpleDoubleProperty(0.0);
    private SimpleDoubleProperty startBal = new SimpleDoubleProperty(0.0);
    private SimpleDoubleProperty balance = new SimpleDoubleProperty(0.0);

    public LineItem() {
    }

    public LineItem(int type, LocalDate date, String parent, String category, double Actual, double Budget) {
        this.type = type;
        this.date.set(date);
        this.parent.set(parent);
        this.category.set(category);
        this.actual.set(Actual);
        this.budget.set(Budget);
    }

    /********************** id **********************************/
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /********************* hide ***********************************/
    public Boolean getHide() {
        return this.hide;
    }

    public void setHide(Boolean hide) {
        this.hide = hide;
    }

    /********************* isCategory ***********************************/
    public Boolean isCategory() {
        return this.isCategory;
    }

    public void setIsCategory(Boolean isCategory) {
        this.isCategory = isCategory;
    }

    /********************* type ***********************************/
    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /********************* date ***********************************/
    public SimpleObjectProperty<LocalDate> getDateProperty() {
        return this.date;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate newDate) {
        this.date.set(newDate);
    }

    /********************* parent ***********************************/
    public StringProperty getParentProperty() {
        return this.parent;
    }

    public String getParent() {
        return this.parent.get();
    }

    public void setParent(String parent) {
        this.parent.set(parent);
    }

    /******************** category ************************************/
    public StringProperty getCategoryProperty() {
        return this.category;
    }

    public String getCategory() {
        if (isCategory) {
            return category.get();
        }
        else {
            return " ".repeat(6) + category.get();
        }
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    /********************* Actual ***********************************/

    public SimpleDoubleProperty getActualProperty() {
        return this.actual;
    }

    public void setActual(Double Actual) {
        this.actual.set(Actual);
        this.balance.set(getStartBal() - getActual());
    }

    public Double getActual() {
        return this.actual.get();
    }

    /********************* Budget ***********************************/
    public SimpleDoubleProperty getBudgetProperty() {
        return this.budget;
    }

    public void setBudget(Double budget) {
        this.budget.set(budget);
    }

    public Double getBudget() {
        return this.budget.get();
    }

    /********************* Diff ***********************************/

    public Double getDiff() {
        if (getType() == 0) {
            return getActual() - getBudget();
        }
        else {
            return getBudget() - getActual();
        }
    }

    /********************* Balance ***********************************/
    public SimpleDoubleProperty getBalanceProperty() {
        return this.balance;
    }

    public void setBalance() {
        this.balance.set(getStartBal() - getActual());
    }

    public Double getBalance() {
        return this.balance.get();
    }

    /********************* startBal ***********************************/
    public SimpleDoubleProperty getstartBalProperty() {
        return this.startBal;
    }

    public void setStartBal(Double startBal) {
        this.startBal.set(startBal);
        this.balance.set(getStartBal() - getActual());
    }

    public Double getStartBal() {
        return this.startBal.get();
    }

    /************************ computed ******************************/
    public Double getComputed() {
        return getStartBal() - getActual();
    }

    @Override
    public String toString() {
        return "{" + " id='" + getId() + "'" + ", hide='" + getHide() + "'" + ", type='" + getType() + "'" + ", date='"
                + getDate() + "'" + ", parent='" + getParent() + "'" + ", category='" + getCategory() + "'"
                + ", actual='" + getActual() + "'" + ", budget='" + getBudget() + "'" + ", diff='" + getDiff() + "'"
                + ", start balance='" + getStartBal() + "'" + "}";
    }

    // convert the LineItem object to LineItemCSV object
    public LineItemCSV toLineItemCSV() {
        LineItemCSV item = new LineItemCSV();
        item.setId(0);
        item.setType(this.getType());
        item.setDate(this.getDate());
        item.setParent(this.getParent());
        item.setCategory(this.getCategory());
        item.setAmount(this.getActual());
        return item;
    }
}