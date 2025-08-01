package com.budget;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

import com.budget.dataModal.LineItem;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Utility class for handling currency conversion and table cell formatting.
 */
public class Util {

    /**
     * Returns a currency converter for use in table cells.
     * 
     * @return A currency converter.
     */
    public static StringConverter<Double> getCurrencyConverter() {
        return new StringConverter<Double>() {
            private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

            {
                numberFormat.setMinimumFractionDigits(2);
                numberFormat.setMaximumFractionDigits(2);
            }

            @Override
            public String toString(Double object) {
                return object == null ? "" : numberFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                try {
                    return string == null || string.isEmpty() ? null : numberFormat.parse(string).doubleValue();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static <T> Callback<TableColumn<T, Double>, TableCell<T, Double>> getRightAlignedCellFactory(
            StringConverter<Double> converter) {
        return column -> {
            TableCell<T, Double> cell = new TextFieldTableCell<>(converter);
            cell.setStyle("-fx-alignment: CENTER-RIGHT;");
            return cell;
        };
    }

    /**
     * Recursively prints all items in a TreeItem structure
     * 
     * @param item The root TreeItem to print
     */
    public static void printTreeItems(TreeItem<LineItem> item) {
        printTreeItems(item, 0);
    }

    /**
     * Recursively prints all items in a TreeItem structure with indentation
     * 
     * @param item  The TreeItem to print
     * @param depth The current depth for indentation
     */
    private static void printTreeItems(TreeItem<LineItem> item, int depth) {
        if (item == null) {
            return;
        }

        // Create indentation based on depth
        String indent = "  ".repeat(depth);

        // Print the current item
        LineItem lineItem = item.getValue();
        if (lineItem != null) {
            System.out.println(indent + "├─ " + lineItem.getCategory() + " (Actual: " + lineItem.getActual()
                    + ", Budget: " + lineItem.getBudget() + ", Type: " + lineItem.getType() + ")");
        }
        else {
            System.out.println(indent + "├─ [NULL ITEM]");
        }

        // Recursively print all children
        for (TreeItem<LineItem> child : item.getChildren()) {
            printTreeItems(child, depth + 1);
        }
    }

    /**
     * Calculates and updates the totals (actual and budget) for each parent
     * TreeItem based on the sum of all its children TreeItems.
     * 
     * @param rootNode The root TreeItem to process
     */
    public static void calculateTreeTotals(TreeItem<LineItem> rootNode) {
        if (rootNode == null)
            return;

        // Process each parent node (direct children of root)
        for (TreeItem<LineItem> parentNode : rootNode.getChildren()) {
            calculateNodeTotal(parentNode);
        }

        // Calculate root total from all parent totals
        if (rootNode.getValue() != null) {
            double rootActualTotal = 0.0;
            double rootBudgetTotal = 0.0;

            for (TreeItem<LineItem> parentNode : rootNode.getChildren()) {
                LineItem parentItem = parentNode.getValue();
                if (parentItem != null) {
                    rootActualTotal += parentItem.getActual();
                    rootBudgetTotal += parentItem.getBudget();
                }
            }

            rootNode.getValue().setActual(rootActualTotal);
            rootNode.getValue().setBudget(rootBudgetTotal);
        }
    }

    /**
     * Recursively calculates totals for a TreeItem node based on its children.
     * 
     * @param node The TreeItem node to calculate totals for
     */
    private static void calculateNodeTotal(TreeItem<LineItem> node) {
        if (node == null || node.getValue() == null)
            return;

        double actualTotal = 0.0;
        double budgetTotal = 0.0;

        // If this node has children, calculate totals from children
        if (!node.getChildren().isEmpty()) {
            for (TreeItem<LineItem> child : node.getChildren()) {
                // Recursively calculate child totals first
                calculateNodeTotal(child);

                LineItem childItem = child.getValue();
                if (childItem != null) {
                    actualTotal += childItem.getActual();
                    budgetTotal += childItem.getBudget();
                }
            }

            // Update this node's totals
            node.getValue().setActual(actualTotal);
            node.getValue().setBudget(budgetTotal);
        }
        // If no children, the node keeps its own values (leaf node)
    }

    public static String getMonthString(LocalDate date) {
        return String.format("%02d", date.getMonthValue());
    }

    public static String getYearString(LocalDate date) {
        return String.format("%04d", date.getYear());

    }
}
