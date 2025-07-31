package com.budget.dataModal;

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
    
    // Utility methods
    public boolean isNegative() {
        return runningTotal < 0;
    }
    
    public boolean isOverMaximum() {
        return maximumAmount != null && runningTotal > maximumAmount;
    }
    
    public boolean needsWarning() {
        return isNegative() && !warningIssued;
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