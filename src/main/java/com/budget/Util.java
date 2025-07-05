package com.budget;

import java.text.NumberFormat;
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
     * @param item The root TreeItem to print
     */
    public static void printTreeItems(TreeItem<LineItem> item) {
        printTreeItems(item, 0);
    }

    /**
     * Recursively prints all items in a TreeItem structure with indentation
     * @param item The TreeItem to print
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
            System.out.println(indent + "├─ " + lineItem.getCategory() + 
                             " (Actual: " + lineItem.getActual() + 
                             ", Budget: " + lineItem.getBudget() + 
                             ", Type: " + lineItem.getType() + ")");
        } else {
            System.out.println(indent + "├─ [NULL ITEM]");
        }

        // Recursively print all children
        for (TreeItem<LineItem> child : item.getChildren()) {
            printTreeItems(child, depth + 1);
        }
    }

}
  
