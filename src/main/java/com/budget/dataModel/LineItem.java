package com.budget.dataModel;

import java.time.LocalDate;
import java.util.Objects;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a line item in the budget application with properties for financial data.
 * This class uses JavaFX properties to support data binding and automatic UI updates.
 */
public class LineItem {
    
    // ========================= CONSTANTS =========================
    
    /** Default indentation for non-category items */
    private static final String CATEGORY_INDENT = " ".repeat(6);
    private static final int INCOME = 0;
    
    // ========================= FIELDS =========================
    
    private int id = 0;
    private int categoryId = 0; // Used for database reference
    private int acct = 0;
    private int type = INCOME;

    private boolean hide = false;
    private boolean includeInTotal = true;
    private boolean isCategory = false;
    
    // Using JavaFX properties for better data binding
    private final SimpleObjectProperty<LocalDate> date = new SimpleObjectProperty<>(LocalDate.now());
    private final SimpleStringProperty parent = new SimpleStringProperty("");
    private final SimpleStringProperty category = new SimpleStringProperty("");
    private final SimpleDoubleProperty actual = new SimpleDoubleProperty(0.0);
    private final SimpleDoubleProperty budget = new SimpleDoubleProperty(0.0);
    private final SimpleDoubleProperty startBal = new SimpleDoubleProperty(0.0);
    private final SimpleDoubleProperty balance = new SimpleDoubleProperty(0.0);

    // ========================= CONSTRUCTORS =========================
    
    /**
     * Default constructor creating an empty LineItem.
     */
    public LineItem() {
        // Initialize balance calculation listener
        setupBalanceCalculation();
    }

    /**
     * Creates a LineItem with specified values.
     * 
     * @param type The item type (0=Income, 1=Mandatory, 2=Discretionary)
     * @param date The date for this line item
     * @param parent The parent category name
     * @param category The category name
     * @param actual The actual amount
     * @param budget The budget amount
     */
    public LineItem(int type, LocalDate date, String parent, String category, double actual, double budget) {
        this();
        this.type = type;
        this.date.set(date);
        this.parent.set(parent != null ? parent : "");
        this.category.set(category != null ? category : "");
        this.actual.set(actual);
        this.budget.set(budget);
        setupBalanceCalculation();
    }

    /**
     * Copy constructor to create a deep copy of another LineItem.
     * 
     * @param other The LineItem to copy
     */
    public LineItem(LineItem other) {
        this();
        if (other != null) {
            this.id = other.id;
            this.categoryId = other.categoryId;
            this.acct = other.acct;
            this.type = other.type;
            this.hide(other.hide());
            this.includeInTotal(other.includeInTotal());
            this.isCategory(other.isCategory());
            this.date.set(other.getDate());
            this.parent.set(other.getParent());
            this.category.set(other.getCategoryRaw()); // Use raw category without formatting
            this.actual.set(other.getActual());
            this.budget.set(other.getBudget());
            this.startBal.set(other.getStartBal());
            this.balance.set(other.getBalance());
        }
    }

    /**
     * Sets up automatic balance calculation when actual or startBal changes.
     */
    private void setupBalanceCalculation() {
        actual.addListener((obs, oldVal, newVal) -> updateBalance());
        startBal.addListener((obs, oldVal, newVal) -> updateBalance());
    }

    /**
     * Updates the balance based on current startBal and actual values.
     */
    private void updateBalance() {
        this.balance.set(getStartBal() - getActual());
    }

    // ========================= ID PROPERTIES =========================
    
    /**
     * Gets the unique identifier for this line item.
     * 
     * @return The line item ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the unique identifier for this line item.
     * 
     * @param id The line item ID
     */
    public void setId(int id) {
        this.id = id;
    }

        // ========================= CAT_ID PROPERTIES =========================
    
    /**
     * Gets the unique identifier for this line item.
     * 
     * @return The line item ID
     */
    public int getCatagoryId() {
        return this.categoryId;
    }

    /**
     * Sets the unique identifier for this line item.
     * 
     * @param id The line item ID
     */
    public void setCategoryId(int id) {
        this.categoryId = id;
    }


    // ========================= INCLUDE IN TOTAL PROPERTIES =========================
 
    /**
     * Checks if this item should be included in totals.
     * 
     * @return true if included in totals, false otherwise
     */
    public boolean includeInTotal() {
        return includeInTotal;
    }

    /**
     * Sets whether this item should be included in totals.
     * 
     * @param includeInTotal true to include in totals, false otherwise
     */
    public void includeInTotal(boolean includeInTotal) {
        this.includeInTotal = includeInTotal;
    }

    // ========================= HIDE PROPERTIES =========================
 
    /**
     * Checks if this item is hidden.
     * 
     * @return true if hidden, false otherwise
     */
    public boolean hide() {
        return hide;
    }

    /**
     * Sets the hide status of this item.
     * 
     * @param hide true to hide, false to show
     */
    public void hide(boolean hide) {
        this.hide = hide;
    }

 
    // ========================= CATEGORY TYPE PROPERTIES =========================
     /**
     * Checks if this item represents a category.
     * 
     * @return true if this is a category, false otherwise
     */
    public boolean isCategory() {
        return isCategory;
    }

    /**
     * Sets whether this item represents a category.
     * 
     * @param isCategory true if this is a category, false otherwise
     */
    public void isCategory(boolean isCategory) {
        this.isCategory = isCategory;
    }

    // ========================= ACCOUNT PROPERTIES =========================
    
    /**
     * Gets the account number.
     * 
     * @return The account number
     */
    public int getAcct() {
        return this.acct;
    }

    /**
     * Sets the account number.
     * 
     * @param acct The account number
     */
    public void setAcct(int acct) {
        this.acct = acct; 
    }

    // ========================= TYPE PROPERTIES =========================
    
    /**
     * Gets the item type.
     * 
     * @return The type (0=Income, 1=Mandatory, 2=Discretionary)
     */
    public int getType() {
        return this.type;
    }

    /**
     * Sets the item type.
     * 
     * @param type The type (0=Income, 1=Mandatory, 2=Discretionary)
     */
    public void setType(int type) {
        this.type = type;
    }

    // ========================= DATE PROPERTIES =========================
    
    /**
     * Gets the date property for data binding.
     * 
     * @return The SimpleObjectProperty for date
     */
    public SimpleObjectProperty<LocalDate> getDateProperty() {
        return this.date;
    }

    /**
     * Gets the date value.
     * 
     * @return The date
     */
    public LocalDate getDate() {
        return date.get();
    }

    /**
     * Sets the date value.
     * 
     * @param newDate The new date
     */
    public void setDate(LocalDate newDate) {
        this.date.set(newDate != null ? newDate : LocalDate.now());
    }

    // ========================= PARENT PROPERTIES =========================
    
    /**
     * Gets the parent property for data binding.
     * 
     * @return The StringProperty for parent
     */
    public StringProperty getParentProperty() {
        return this.parent;
    }

    /**
     * Gets the parent category name.
     * 
     * @return The parent category name
     */
    public String getParent() {
        return this.parent.get();
    }

    /**
     * Sets the parent category name.
     * 
     * @param parent The parent category name
     */
    public void setParent(String parent) {
        this.parent.set(parent != null ? parent : "");
    }

    // ========================= CATEGORY PROPERTIES =========================
    
    /**
     * Gets the category property for data binding.
     * 
     * @return The StringProperty for category
     */
    public StringProperty getCategoryProperty() {
        return this.category;
    }

    /**
     * Gets the category name with formatting (indented if not a main category).
     * 
     * @return The formatted category name
     */
    public String getCategory() {
        String categoryName = category.get();
        return isCategory() ? categoryName : CATEGORY_INDENT + categoryName;
    }

    /**
     * Gets the raw category name without formatting.
     * 
     * @return The raw category name
     */
    public String getCategoryRaw() {
        return category.get();
    }

    /**
     * Sets the category name.
     * 
     * @param category The category name
     */
    public void setCategory(String category) {
        this.category.set(category != null ? category : "");
    }

    // ========================= ACTUAL AMOUNT PROPERTIES =========================
    
    /**
     * Gets the actual amount property for data binding.
     * 
     * @return The SimpleDoubleProperty for actual amount
     */
    public SimpleDoubleProperty getActualProperty() {
        return this.actual;
    }

    /**
     * Gets the actual amount.
     * 
     * @return The actual amount
     */
    public Double getActual() {
        return this.actual.get();
    }

    /**
     * Sets the actual amount and updates the balance.
     * 
     * @param actual The actual amount
     */
    public void setActual(Double actual) {
        this.actual.set(actual != null ? actual : 0.0);
    }

    // ========================= BUDGET AMOUNT PROPERTIES =========================
    
    /**
     * Gets the budget amount property for data binding.
     * 
     * @return The SimpleDoubleProperty for budget amount
     */
    public SimpleDoubleProperty getBudgetProperty() {
        return this.budget;
    }

    /**
     * Gets the budget amount.
     * 
     * @return The budget amount
     */
    public Double getBudget() {
        return this.budget.get();
    }

    /**
     * Sets the budget amount.
     * 
     * @param budget The budget amount
     */
    public void setBudget(Double budget) {
        this.budget.set(budget != null ? budget : 0.0);
    }

    // ========================= DIFFERENCE CALCULATION =========================
    
    /**
     * Calculates the difference between budget and actual.
     * For income (type 0): actual - budget (positive means over budget)
     * For expenses (type 1,2): budget - actual (positive means under budget)
     * 
     * @return The calculated difference
     */
    public Double getDiff() {
        if (getType() == 0) {
            return getActual() - getBudget();
        } else {
            return getBudget() - getActual();
        }
    }

    // ========================= BALANCE PROPERTIES =========================
    
    /**
     * Gets the balance property for data binding.
     * 
     * @return The SimpleDoubleProperty for balance
     */
    public SimpleDoubleProperty getBalanceProperty() {
        return this.balance;
    }

    /**
     * Gets the current balance.
     * 
     * @return The balance (startBal - actual)
     */
    public Double getBalance() {
        return this.balance.get();
    }

    /**
     * Manually triggers balance recalculation.
     */
    public void setBalance() {
        updateBalance();
    }

    // ========================= START BALANCE PROPERTIES =========================
    
    /**
     * Gets the start balance property for data binding.
     * 
     * @return The SimpleDoubleProperty for start balance
     */
    public SimpleDoubleProperty getStartBalProperty() { // Fixed method name
        return this.startBal;
    }

    /**
     * Gets the starting balance.
     * 
     * @return The starting balance
     */
    public Double getStartBal() {
        return this.startBal.get();
    }

    /**
     * Sets the starting balance and updates the computed balance.
     * 
     * @param startBal The starting balance
     */
    public void setStartBal(Double startBal) {
        this.startBal.set(startBal != null ? startBal : 0.0);
    }

    // ========================= COMPUTED VALUES =========================
    
    /**
     * Computes the balance (startBal - actual).
     * This is the same as getBalance() but provided for backward compatibility.
     * 
     * @return The computed balance
     */
    public Double getComputed() {
        return getStartBal() - getActual();
    }

    // ========================= UTILITY METHODS =========================
    
    /**
     * Converts this LineItem to a LineItemCSV object.
     * 
     * @return A new LineItemCSV object with data from this LineItem
     */
    public LineItemCSV toLineItemCSV() {
        LineItemCSV item = new LineItemCSV();
        item.setId(0); // Reset ID for CSV export
        item.setType(this.getType());
        item.setDate(this.getDate());
        item.setParent(this.getParent());
        item.setCategory(this.getCategoryRaw()); // Use raw category without formatting
        item.setAmount(this.getActual());
        return item;
    }

    /**
     * Creates a deep copy of this LineItem.
     * 
     * @return A new LineItem with the same values
     */
    public LineItem copy() {
        return new LineItem(this);
    }

    // ========================= OBJECT METHODS =========================
    
    /**
     * Checks if this LineItem is equal to another object.
     * 
     * @param obj The object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        LineItem lineItem = (LineItem) obj;
        return id == lineItem.id &&
                categoryId == lineItem.categoryId &&
               acct == lineItem.acct &&
               type == lineItem.type &&
               hide() == lineItem.hide() &&
               includeInTotal() == lineItem.includeInTotal() &&
               isCategory() == lineItem.isCategory() &&
               Objects.equals(getDate(), lineItem.getDate()) &&
               Objects.equals(getParent(), lineItem.getParent()) &&
               Objects.equals(getCategoryRaw(), lineItem.getCategoryRaw()) &&
               Objects.equals(getActual(), lineItem.getActual()) &&
               Objects.equals(getBudget(), lineItem.getBudget()) &&
               Objects.equals(getStartBal(), lineItem.getStartBal());
    }

    /**
     * Generates a hash code for this object.
     * 
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, categoryId, acct, type, hide(), includeInTotal(), isCategory(),
                           getDate(), getParent(), getCategoryRaw(), getActual(), 
                           getBudget(), getStartBal());
    }

    /**
     * Returns a string representation of this LineItem.
     * 
     * @return A formatted string with all property values
     */
    @Override
    public String toString() {
        return String.format("LineItem{id=%d, categoryId=%d,   acct=%d, type=%d, hide=%s, includeInTotal=%s, " +
                           "isCategory=%s, date='%s', parent='%s', category='%s', actual=%.2f, " +
                           "budget=%.2f, diff=%.2f, startBal=%.2f, balance=%.2f}",
                           getId(), getCatagoryId(), getAcct(), getType(), hide(), includeInTotal(), isCategory(),
                           getDate(), getParent(), getCategoryRaw(), getActual(),
                           getBudget(), getDiff(), getStartBal(), getBalance());
    }
}