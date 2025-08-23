package com.budget.dataModal;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a CSV line item for budget data import/export operations.
 * This class is specifically designed for CSV file processing and data transfer.
 */
public class LineItemCSV {
    
    // ========================= FIELDS =========================
    
    private int id = 0;
    private boolean hide = false;
    private boolean includeInTotal = true;
    private boolean isMainCategory = true;
    private int type = 0;
    private LocalDate date = LocalDate.now();
    private String parent = "";
    private String category = "";
    private double amount = 0.0;

    // ========================= BOOLEAN PROPERTIES =========================

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public void setIncludeInTotal(boolean includeInTotal) {
        this.includeInTotal = includeInTotal;
    }

    // ========================= CONSTRUCTORS =========================
    
    /**
     * Default constructor creating an empty LineItemCSV.
     */
    public LineItemCSV() {
        // Default constructor with field initialization
    }

    /**
     * Creates a LineItemCSV with specified core values.
     * 
     * @param type The item type (0=Income, 1=Mandatory, 2=Discretionary)
     * @param date The date for this line item
     * @param parent The parent category name
     * @param category The category name
     * @param amount The amount value
     */
    public LineItemCSV(int type, LocalDate date, String parent, String category, double amount) {
        this.type = type;
        this.date = date != null ? date : LocalDate.now();
        this.parent = parent != null ? parent : "";
        this.category = category != null ? category : "";
        this.amount = amount;
    }

    /**
     * Copy constructor to create a deep copy of another LineItemCSV.
     * 
     * @param other The LineItemCSV to copy
     */
    public LineItemCSV(LineItemCSV other) {
        if (other != null) {
            this.id = other.id;
            this.hide = other.hide;
            this.includeInTotal = other.includeInTotal;
            this.isMainCategory = other.isMainCategory;
            this.type = other.type;
            this.date = other.date;
            this.parent = other.parent;
            this.category = other.category;
            this.amount = other.amount;
        }
    }

    // ========================= ID PROPERTIES =========================
    
    /**
     * Gets the unique identifier for this CSV line item.
     * 
     * @return The line item ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the unique identifier for this CSV line item.
     * 
     * @param id The line item ID
     */
    public void setId(int id) {
        this.id = id;
    }

    // ========================= HIDE PROPERTIES =========================
    
    /**
     * Checks if this item is hidden.
     * 
     * @return true if hidden, false otherwise
     */
    public boolean hide() {
        return this.hide;
    }

    /**
     * Sets the hide status of this item.
     * 
     * @param hide true to hide, false to show
     */
    public void hide(boolean hide) {
        this.hide = hide;
    }

    // ========================= INCLUDE IN TOTAL PROPERTIES =========================
    
    /**
     * Checks if this item should be included in totals.
     * 
     * @return true if included in totals, false otherwise
     */
    public boolean includeInTotal() {
        return this.includeInTotal;
    }

    /**
     * Sets whether this item should be included in totals.
     * 
     * @param includeInTotal true to include in totals, false otherwise
     */
    public void includeInTotal(boolean includeInTotal) {
        this.includeInTotal = includeInTotal;
    }


    // Legacy methods for backward compatibility
    public boolean include_in_total() {
        return includeInTotal();
    }

    public void include_in_total(boolean includeInTotal) {
        includeInTotal(includeInTotal);
    }

    // ========================= MAIN CATEGORY PROPERTIES =========================
    
    /**
     * Checks if this item is a main category.
     * 
     * @return true if this is a main category, false otherwise
     */
    public boolean isMainCategory() {
        return this.isMainCategory;
    }

    /**
     * Sets whether this item is a main category.
     * 
     * @param isMainCategory true if this is a main category, false otherwise
     */
    public void isMainCategory(boolean isMainCategory) {
        this.isMainCategory = isMainCategory;
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
     * Gets the date value.
     * 
     * @return The date
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * Sets the date value.
     * 
     * @param date The new date
     */
    public void setDate(LocalDate date) {
        this.date = date != null ? date : LocalDate.now();
    }

    // ========================= PARENT PROPERTIES =========================
    
    /**
     * Gets the parent category name.
     * 
     * @return The parent category name
     */
    public String getParent() {
        return this.parent;
    }

    /**
     * Sets the parent category name.
     * 
     * @param parent The parent category name
     */
    public void setParent(String parent) {
        this.parent = parent != null ? parent : "";
    }

    // ========================= CATEGORY PROPERTIES =========================
    
    /**
     * Gets the category name.
     * 
     * @return The category name
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * Sets the category name.
     * 
     * @param category The category name
     */
    public void setCategory(String category) {
        this.category = category != null ? category : "";
    }

    // ========================= AMOUNT PROPERTIES =========================
    
    /**
     * Gets the amount value.
     * 
     * @return The amount
     */
    public double getAmount() {
        return this.amount;
    }

    /**
     * Sets the amount value.
     * 
     * @param amount The amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    // ========================= UTILITY METHODS =========================
    
    /**
     * Converts this LineItemCSV to a LineItem object.
     * 
     * @return A new LineItem object with data from this LineItemCSV
     */
    public LineItem toLineItem() {
        LineItem item = new LineItem(this.type, this.date, this.parent, this.category, this.amount, 0.0);
        item.setId(this.id);
        item.hide(this.hide);
        item.includeInTotal(this.includeInTotal);
        item.isCategory(this.isMainCategory);
        return item;
    }

    /**
     * Creates a deep copy of this LineItemCSV.
     * 
     * @return A new LineItemCSV with the same values
     */
    public LineItemCSV copy() {
        return new LineItemCSV(this);
    }

    /**
     * Validates if this LineItemCSV has all required fields set.
     * 
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return this.category != null && !this.category.trim().isEmpty() &&
               this.parent != null && !this.parent.trim().isEmpty() &&
               this.date != null;
    }

    /**
     * Resets this LineItemCSV to default values.
     */
    public void reset() {
        this.id = 0;
        this.hide = false;
        this.includeInTotal = true;
        this.isMainCategory = true;
        this.type = 0;
        this.date = LocalDate.now();
        this.parent = "";
        this.category = "";
        this.amount = 0.0;
    }

    // ========================= OBJECT METHODS =========================
    
    /**
     * Checks if this LineItemCSV is equal to another object.
     * 
     * @param obj The object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        LineItemCSV that = (LineItemCSV) obj;
        return id == that.id &&
               hide == that.hide &&
               includeInTotal == that.includeInTotal &&
               isMainCategory == that.isMainCategory &&
               type == that.type &&
               Double.compare(that.amount, amount) == 0 &&
               Objects.equals(date, that.date) &&
               Objects.equals(parent, that.parent) &&
               Objects.equals(category, that.category);
    }

    /**
     * Generates a hash code for this object.
     * 
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, hide, includeInTotal, isMainCategory, type, date, parent, category, amount);
    }

    /**
     * Returns a string representation of this LineItemCSV.
     * 
     * @return A formatted string with all property values
     */
    @Override
    public String toString() {
        return String.format("LineItemCSV{id=%d, hide=%s, includeInTotal=%s, isMainCategory=%s, " +
                           "type=%d, date='%s', parent='%s', category='%s', amount=%.2f}",
                           id, hide, includeInTotal, isMainCategory, type, date, parent, category, amount);
    }

    /**
     * Returns a CSV-formatted string representation.
     * 
     * @return A CSV string with values separated by commas
     */
    public String toCsvString() {
        return String.format("%d,%s,%s,%s,%d,%s,%s,%s,%.2f",
                           id, hide, includeInTotal, isMainCategory, type, date, parent, category, amount);
    }

    /**
     * Returns the CSV header row.
     * 
     * @return A CSV header string
     */
    public static String getCsvHeader() {
        return "id,hide,includeInTotal,isMainCategory,type,date,parent,category,amount";
    }
}