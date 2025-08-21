package com.budget.dataModal;

import javafx.scene.control.TreeItem;

/**
 * Data container for database results to ensure thread-safe data transfer.
 */
public class DatabaseDataResult {
    private final TreeItem<LineItem> incomeRoot;
    private final TreeItem<LineItem> mandatoryRoot;
    private final TreeItem<LineItem> discretionaryRoot;
    private final LineItem incomeTotals;
    private final LineItem mandatoryTotals;
    private final LineItem discretionaryTotals;

    public DatabaseDataResult(TreeItem<LineItem> incomeRoot, TreeItem<LineItem> mandatoryRoot,
            TreeItem<LineItem> discretionaryRoot, LineItem incomeTotals, LineItem mandatoryTotals,
            LineItem discretionaryTotals) {
        this.incomeRoot = incomeRoot;
        this.mandatoryRoot = mandatoryRoot;
        this.discretionaryRoot = discretionaryRoot;
        this.incomeTotals = incomeTotals;
        this.mandatoryTotals = mandatoryTotals;
        this.discretionaryTotals = discretionaryTotals;
    }

    public DatabaseDataResult(TreeItem<LineItem> incomeRoot, TreeItem<LineItem> mandatoryRoot,
            TreeItem<LineItem> discretionaryRoot) {
        this.incomeRoot = incomeRoot;
        this.mandatoryRoot = mandatoryRoot;
        this.discretionaryRoot = discretionaryRoot;

        this.incomeTotals = incomeRoot.getValue();
        this.mandatoryTotals = mandatoryRoot.getValue();
        this.discretionaryTotals = discretionaryRoot.getValue();
    }

    // Getters...
    public TreeItem<LineItem> getIncomeRoot() {
        return incomeRoot;
    }

    public TreeItem<LineItem> getMandatoryRoot() {
        return mandatoryRoot;
    }

    public TreeItem<LineItem> getDiscretionaryRoot() {
        return discretionaryRoot;
    }



    public LineItem getIncomeTotals() {
        return incomeTotals;
    }

    public LineItem getMandatoryTotals() {
        return mandatoryTotals;
    }

    public LineItem getDiscretionaryTotals() {
        return discretionaryTotals;
    }
}