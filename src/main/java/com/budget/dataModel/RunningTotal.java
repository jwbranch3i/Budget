package com.budget.dataModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a running total for a category across months
 */
public class RunningTotal {
    
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    
    private int id;
    private int categoryId;
    private String categoryName;
    private LocalDate monthDate;
    private double previousBalance;
    private double currentDifference; // budget - actual
    private double runningTotal;
    private Double maximumAmount; // Optional
    private boolean warningIssued;
    private Double modifiedRunningTotal;
    
    // Constructors
    public RunningTotal() {}
    
    public RunningTotal(int categoryId, LocalDate monthDate) {
        this.categoryId = categoryId;
        this.monthDate = monthDate;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public LocalDate getMonthDate() { return monthDate; }
    public void setMonthDate(LocalDate monthDate) { this.monthDate = monthDate; }
    
    public String getMonthDateString() {
        return monthDate != null ? monthDate.format(MONTH_FORMATTER) : null;
    }
    
    public void setMonthDateFromString(String monthStr) {
        this.monthDate = LocalDate.parse(monthStr + "-01");
    }
    
    public double getPreviousBalance() { return previousBalance; }
    public void setPreviousBalance(double previousBalance) { this.previousBalance = previousBalance; }
    
    public double getCurrentDifference() { return currentDifference; }
    public void setCurrentDifference(double currentDifference) { this.currentDifference = currentDifference; }
    
    public double getRunningTotal() { return runningTotal; }
    public void setRunningTotal(double runningTotal) { this.runningTotal = runningTotal; }
    
    public Double getMaximumAmount() { return maximumAmount; }
    public void setMaximumAmount(Double maximumAmount) { this.maximumAmount = maximumAmount; }
    
    public boolean isWarningIssued() { return warningIssued; }
    public void setWarningIssued(boolean warningIssued) { this.warningIssued = warningIssued; }
    
    public Double getModifiedRunningTotal() {
        return modifiedRunningTotal;
    }
    
    public void setModifiedRunningTotal(Double modifiedRunningTotal) {
        this.modifiedRunningTotal = modifiedRunningTotal;
    }
    
    /**
     * Gets the effective running total - uses modified if available, otherwise original.
     */
    public double getEffectiveRunningTotal() {
        return isModified() ? modifiedRunningTotal : runningTotal;
    }
    
    /**
     * Checks if this running total has been manually modified.
     */
    public boolean isModified() {
        return modifiedRunningTotal != null;
    }
    
    /**
     * Clears the manual modification, reverting to calculated running total.
     */
    public void clearModification() {
        this.modifiedRunningTotal = null;
    }
    
    // Utility methods
    public boolean isNegative() {
        return runningTotal < 0;
    }
    
    public boolean isOverMaximum() {
        return maximumAmount != null && getEffectiveRunningTotal() > maximumAmount;
    }
    
    public boolean needsWarning() {
        return getEffectiveRunningTotal() < 0.0 && !warningIssued;
    }
    
    public void calculateRunningTotal() {
        this.runningTotal = this.previousBalance + this.currentDifference;
    }
    
    @Override
    public String toString() {
        return String.format("RunningTotal{category='%s', month='%s', total=%.2f}", 
                           categoryName, getMonthDateString(), runningTotal);
    }
}