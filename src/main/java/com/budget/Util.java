package com.budget;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.budget.controllers.PrimaryController;
import com.budget.dataModel.LineItem;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
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
    private static final Logger LOGGER = Logger.getLogger(PrimaryController.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

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
    public static void calculateTreeTotals(TreeItem<LineItem> rootNode) /**/ {
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
            double rootRunningTotalTotal = 0.0;

            for (TreeItem<LineItem> parentNode : rootNode.getChildren()) {
                LineItem parentItem = parentNode.getValue();
                if (parentItem != null && !parentItem.hide()) {
                    rootActualTotal += parentItem.getActual();
                    rootBudgetTotal += parentItem.getBudget();
                    rootRunningTotalTotal += parentItem.getRunningTotal();
                }
            }

            rootNode.getValue().setActual(rootActualTotal);
            rootNode.getValue().setBudget(rootBudgetTotal);
            rootNode.getValue().setRunningTotal(rootRunningTotalTotal);
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
        double rootRunningTotalTotal = 0.0;

        // If this node has children, calculate totals from children
        if (!node.getChildren().isEmpty()) {
            for (TreeItem<LineItem> child : node.getChildren()) {
                // Recursively calculate child totals first
                calculateNodeTotal(child);

                LineItem childItem = child.getValue();
                if (childItem != null) {
                    actualTotal += childItem.getActual();
                    budgetTotal += childItem.getBudget();
                    rootRunningTotalTotal += childItem.getRunningTotal();
                }
            }

            // Update this node's totals
            node.getValue().setActual(actualTotal);
            node.getValue().setBudget(budgetTotal);
            node.getValue().setRunningTotal(rootRunningTotalTotal);
        }
    }

    public static String getMonthString(LocalDate date) {
        return String.format("%02d", date.getMonthValue());
    }

    public static String getYearString(LocalDate date) {
        return String.format("%04d", date.getYear());

    }

    /**
     * Formats a LocalDate to the standard database format (YYYY-MM).
     * 
     * @param date The LocalDate to format
     * @return The formatted date string, or null if date is null
     */
    public static String formatDateForDatabase(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    /**
     * Validates that a date string is in the correct YYYY-MM format.
     * 
     * @param dateString The date string to validate
     * @return true if the format is correct, false otherwise
     */
    public static boolean isValidDateFormat(String dateString) {
        if (dateString == null || dateString.length() != 7) {
            return false;
        }

        try {
            LocalDate.parse(dateString + "-01"); // Add day to parse
            return dateString.matches("\\d{4}-\\d{2}");
        }
        catch (Exception e) {
            return false;
        }
    }

    public static void executeAsyncTask(Runnable backgroundTask, Runnable uiTask, String errorMessage) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (backgroundTask != null) {
                    backgroundTask.run();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                if (uiTask != null) {
                    Platform.runLater(uiTask);
                }
            }

            @Override
            protected void failed() {
                LOGGER.log(Level.SEVERE, errorMessage, getException());
                Platform.runLater(() -> showErrorAlert("Operation Error", errorMessage));
            }
        };

        executorService.submit(task);
    }

        // ========================= THREAD POOL =========================

    private static ExecutorService executorService = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("EditItemController-Worker");
        return t;
    });

    // ========================= ALERT UTILITIES =========================

    public static void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show(); // Non-blocking
    }

}
