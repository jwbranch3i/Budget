package com.example.data;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.TableView;

public class UIData {

    public static void updateTable(ArrayList<TableView<LineItem>> tables) {

        tables.get(3).getItems().clear(); // clear totals tableview

        List<LineItem> incomeItems = tables.get(0).getItems();
        List<LineItem> mandatoryItems = tables.get(1).getItems();
        List<LineItem> discretionaryItems = tables.get(2).getItems();

        LineItem incomeTotal = new LineItem();
        if (incomeItems.size() == 1) {
            incomeTotal.setCategory("Total Income");
            incomeTotal.setActual(incomeItems.get(0).getActual());
            incomeTotal.setBudget(incomeItems.get(0).getBudget());
        }

        LineItem mandatoryTotal = new LineItem();
        if (mandatoryItems.size() == 1) {
            mandatoryTotal.setCategory("Total Mandatory");
            mandatoryTotal.setActual(mandatoryItems.get(0).getActual());
            mandatoryTotal.setBudget(mandatoryItems.get(0).getBudget());
        }

        LineItem discretionaryTotal = new LineItem();
        if (discretionaryItems.size() == 1) {
            discretionaryTotal.setCategory("Total Discretionary");
            discretionaryTotal.setActual(discretionaryItems.get(0).getActual());
            discretionaryTotal.setBudget(discretionaryItems.get(0).getBudget());
        }

        LineItem itemTotal = new LineItem();
        itemTotal.setCategory("Total");
        itemTotal.setActual(incomeTotal.getActual() - (mandatoryTotal.getActual() + discretionaryTotal.getActual()));
        itemTotal.setBudget(incomeTotal.getBudget() - (mandatoryTotal.getBudget() + discretionaryTotal.getBudget()));

        tables.get(3).getItems().add(incomeTotal);
        tables.get(3).getItems().add(mandatoryTotal);
        tables.get(3).getItems().add(discretionaryTotal);
        tables.get(3).getItems().add(itemTotal);

    }
}
