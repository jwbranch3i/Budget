package com.budget.dataModal;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

public class UIData {

    public static void updateTableTotal(ArrayList<TableView<LineItem>> tables) {
        updateTableTotal(tables.get(0), tables.get(1), tables.get(2), tables.get(3));
    }
    
    @SuppressWarnings("unchecked")
    public static void updateTableTotal(Object incomeTable, Object mandatoryTable, Object discretionaryTable, TableView<LineItem> totalTable) {

        totalTable.getItems().clear(); // clear totals tableview

        // Get income items (always TableView)
        List<LineItem> incomeItems = ((TableView<LineItem>) incomeTable).getItems();
        
        // Get mandatory items (could be TreeTableView)
        List<LineItem> mandatoryItems = new ArrayList<>();
        if (mandatoryTable instanceof TreeTableView) {
            TreeTableView<LineItem> treeTable = (TreeTableView<LineItem>) mandatoryTable;
            TreeItem<LineItem> root = treeTable.getRoot();
            if (root != null) {
                for (TreeItem<LineItem> child : root.getChildren()) {
                    if (child.getValue() != null) {
                        mandatoryItems.add(child.getValue());
                    }
                }
            }
        } else {
            mandatoryItems = ((TableView<LineItem>) mandatoryTable).getItems();
        }
        
        // Get discretionary items (always TableView)
        List<LineItem> discretionaryItems = ((TableView<LineItem>) discretionaryTable).getItems();

        LineItem incomeTotal = new LineItem();
        if (incomeItems.size() == 1) {
            incomeTotal.setCategory("Total Income");
            incomeTotal.setActual(incomeItems.get(0).getActual());
            incomeTotal.setBudget(incomeItems.get(0).getBudget());
            incomeTotal.setType(0);
        }

        LineItem mandatoryTotal = new LineItem();
        if (mandatoryItems.size() == 1) {
            mandatoryTotal.setCategory("Total Mandatory");
            mandatoryTotal.setActual(mandatoryItems.get(0).getActual());
            mandatoryTotal.setBudget(mandatoryItems.get(0).getBudget());
            mandatoryTotal.setType(1);
        }

        LineItem discretionaryTotal = new LineItem();
        if (discretionaryItems.size() == 1) {
            discretionaryTotal.setCategory("Total Discretionary");
            discretionaryTotal.setActual(discretionaryItems.get(0).getActual());
            discretionaryTotal.setBudget(discretionaryItems.get(0).getBudget());
            discretionaryTotal.setType(2);
        }

        LineItem itemTotal = new LineItem();
        itemTotal.setCategory("Total");
        itemTotal.setActual(incomeTotal.getActual() - (mandatoryTotal.getActual() + discretionaryTotal.getActual()));
        itemTotal.setBudget(incomeTotal.getBudget() - (mandatoryTotal.getBudget() + discretionaryTotal.getBudget()));
        itemTotal.setType(0);
      
        totalTable.getItems().add(incomeTotal);
        totalTable.getItems().add(mandatoryTotal);
        totalTable.getItems().add(discretionaryTotal);
        totalTable.getItems().add(itemTotal);
    }

}
