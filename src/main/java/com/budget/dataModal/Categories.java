package com.budget.dataModal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Represents a budget category with properties for type, parent, category name,
 * and various flags for inclusion and visibility.
 * 
 * This class uses JavaFX properties to support data binding and change notifications.
 */
public class Categories {
    
    // ========================= CONSTANTS =========================
    
    /** Available category types */
    private static final List<String> CATEGORY_TYPES = Collections.unmodifiableList(
        Arrays.asList("Income", "Mandatory", "Discretionary")
    );
    
    // ========================= FIELDS =========================
    
    private int id = 0;
    private boolean includeInTotal = true;
    private boolean hide = false;
    private final SimpleIntegerProperty type = new SimpleIntegerProperty(0);
    private final SimpleStringProperty parent = new SimpleStringProperty("");
    private final SimpleStringProperty category = new SimpleStringProperty("");
    private final SimpleIntegerProperty acct = new SimpleIntegerProperty(0);

    // ========================= CONSTRUCTORS =========================
    
    /**
     * Default constructor creating an empty category.
     */
    public Categories() {
        // Empty constructor - properties are initialized with default values
    }

    /**
     * Creates a category with specified type, parent, and category name.
     * 
     * @param type The category type (0=Income, 1=Mandatory, 2=Discretionary)
     * @param parent The parent category name
     * @param category The category name
     */
    public Categories(Integer type, String parent, String category) {
        if (type != null) {
            this.type.set(type);
        }
        if (parent != null) {
            this.parent.set(parent);
        }
        if (category != null) {
            this.category.set(category);
        }
    }

    /**
     * Copy constructor to create a deep copy of another Categories object.
     * 
     * @param other The Categories object to copy
     */
    public Categories(Categories other) {
        this.id = other.id;
        this.hide(other.hide());
        this.setIncludeInTotal(other.isIncludeInTotal());
        this.type.set(other.type.get());
        this.parent.set(other.parent.get());
        this.category.set(other.category.get());
        this.acct.set(other.acct.get());
    }

    // ========================= ID PROPERTIES =========================
    
    /**
     * Gets the unique identifier for this category.
     * 
     * @return The category ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the unique identifier for this category.
     * 
     * @param id The category ID
     */
    public void setId(int id) {
        this.id = id;
    }

    // ========================= INCLUDE IN TOTAL PROPERTIES =========================
    
  
    /**
     * Checks if this category should be included in totals.
     * 
     * @return true if included in totals, false otherwise
     */
    public boolean isIncludeInTotal() {
        return includeInTotal;
    }

    /**
     * Sets whether this category should be included in totals.
     * 
     * @param includeInTotal true to include in totals, false otherwise
     */
    public void setIncludeInTotal(boolean includeInTotal) {
        this.includeInTotal = includeInTotal;
    }

    // ========================= HIDE PROPERTIES =========================

    /**
     * Checks if this category is hidden.
     * 
     * @return true if hidden, false otherwise
     */
    public boolean hide() {
        return hide;
    }

    /**
     * Sets the hide status of this category.
     * 
     * @param hide true to hide, false to show
     */
    public void hide(boolean hide) {
        this.hide = hide;
    }

    // ========================= TYPE PROPERTIES =========================
    
    /**
     * Gets the type property for data binding.
     * 
     * @return The SimpleIntegerProperty for type
     */
    public SimpleIntegerProperty typeProperty() {
        return this.type;
    }

    /**
     * Gets the category type.
     * 
     * @return The type (0=Income, 1=Mandatory, 2=Discretionary)
     */
    public int getType() {
        return this.type.get();
    }

    /**
     * Sets the category type.
     * 
     * @param type The type (0=Income, 1=Mandatory, 2=Discretionary)
     */
    public void setType(int type) {
        if (type >= 0 && type < CATEGORY_TYPES.size()) {
            this.type.set(type);
        } else {
            throw new IllegalArgumentException("Invalid type: " + type + ". Must be 0, 1, or 2.");
        }
    }

    /**
     * Gets the type name as a string.
     * 
     * @return The type name ("Income", "Mandatory", or "Discretionary")
     */
    public String getTypeName() {
        int typeValue = this.type.get();
        if (typeValue >= 0 && typeValue < CATEGORY_TYPES.size()) {
            return CATEGORY_TYPES.get(typeValue);
        }
        return "Unknown";
    }

    // ========================= ACCOUNT PROPERTIES =========================
    
    /**
     * Gets the account property for data binding.
     * 
     * @return The SimpleIntegerProperty for account
     */
    public SimpleIntegerProperty acctProperty() {
        return this.acct;
    }

    /**
     * Gets the account number.
     * 
     * @return The account number
     */
    public int getAcct() {
        return this.acct.get();
    }

    /**
     * Sets the account number.
     * 
     * @param acct The account number
     */
    public void setAcct(int acct) {
        this.acct.set(acct);
    }

    // ========================= PARENT PROPERTIES =========================
    
    /**
     * Gets the parent property for data binding.
     * 
     * @return The SimpleStringProperty for parent
     */
    public SimpleStringProperty parentProperty() {
        return this.parent;
    }

    /**
     * Gets the parent category name.
     * 
     * @return The parent category name
     */
    public String getParent() {
        return parent.get();
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
     * @return The SimpleStringProperty for category
     */
    public SimpleStringProperty categoryProperty() {
        return this.category;
    }

    /**
     * Gets the category name.
     * 
     * @return The category name
     */
    public String getCategory() {
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

    // ========================= STATIC UTILITY METHODS =========================
    
    /**
     * Gets the list of available category types.
     * 
     * @return Unmodifiable list of category types
     */
    public static List<String> getCategoryTypes() {
        return CATEGORY_TYPES;
    }

    /**
     * Gets the type index for a given type name.
     * 
     * @param typeName The type name to look up
     * @return The type index, or -1 if not found
     */
    public static int getTypeIndex(String typeName) {
        return CATEGORY_TYPES.indexOf(typeName);
    }

    /**
     * Validates if a type index is valid.
     * 
     * @param typeIndex The type index to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidTypeIndex(int typeIndex) {
        return typeIndex >= 0 && typeIndex < CATEGORY_TYPES.size();
    }

    // ========================= OBJECT METHODS =========================
    
    /**
     * Creates a deep copy of this Categories object.
     * 
     * @return A new Categories object with the same values
     */
    public Categories copy() {
        return new Categories(this);
    }

    /**
     * Checks if this category is equal to another object.
     * 
     * @param obj The object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Categories that = (Categories) obj;
        return id == that.id &&
               hide() == that.hide() &&
               isIncludeInTotal() == that.isIncludeInTotal() &&
               getType() == that.getType() &&
               getAcct() == that.getAcct() &&
               getParent().equals(that.getParent()) &&
               getCategory().equals(that.getCategory());
    }

    /**
     * Generates a hash code for this object.
     * 
     * @return The hash code
     */
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (hide() ? 1 : 0);
        result = 31 * result + (isIncludeInTotal() ? 1 : 0);
        result = 31 * result + getType();
        result = 31 * result + getAcct();
        result = 31 * result + getParent().hashCode();
        result = 31 * result + getCategory().hashCode();
        return result;
    }

    /**
     * Returns a string representation of this category.
     * 
     * @return A formatted string with all property values
     */
    @Override
    public String toString() {
        return String.format("Categories{id=%d, hide=%s, includeInTotal=%s, type=%d (%s), " +
                           "parent='%s', category='%s', acct=%d}",
                           getId(), hide(), isIncludeInTotal(), getType(), getTypeName(),
                           getParent(), getCategory(), getAcct());
    }
}