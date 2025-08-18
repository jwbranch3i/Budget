package com.budget.dataModal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.budget.Util;

import javafx.scene.control.TreeItem;
 
public class ReadData {
    private static final Logger LOGGER = Logger.getLogger(ReadData.class.getName());
    // ========================= CONSTANTS =========================

    /**
     * Finds the category of a LineItemCSV in the actual database table.
     * 
     * @param item The LineItemCSV object to find the category for.
     * @return The category ID if found, or -1 if not found.
     */
    public static LineItemCSV actualFindCategory(LineItemCSV item) {
        LineItemCSV returnItem = createCopyWithId(item, -1);

        // Use Util.formatDateForDatabase instead of local DATE_FORMATTER
        String dateString = Util.formatDateForDatabase(item.getDate());

        try (PreparedStatement findRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_FIND_RECORD_BY_ID_AND_DATE)) {
            findRecord.setInt(1, item.getId());
            findRecord.setString(2, dateString);

            try (ResultSet rs = findRecord.executeQuery()) {
                if (rs.next()) {
                    returnItem.setId(rs.getInt(DB.ACTUAL_COL_ID));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in actualFindCategory", e);
            return item;
        }
        return returnItem;
    }

    /**
     * Finds the record index of a LineItemCSV in the category database table.
     * 
     * @param item The LineItemCSV object to find the record index for.
     * @return The record index if found, or -1 if not found.
     */
    public static LineItemCSV categoryFindRecord(LineItemCSV item) {
        LineItemCSV returnItem = createCopyWithId(item, -1);

        try (PreparedStatement psFindRecord = DataSource.getConn().prepareStatement(DB.CAT_FIND_CATEGORY)) {
            psFindRecord.setString(1, item.getParent());
            psFindRecord.setString(2, item.getCategory());

            try (ResultSet rs = psFindRecord.executeQuery()) {
                if (rs.next()) {
                    returnItem.setId(rs.getInt(DB.CAT_COL_ID));
                    returnItem.setType(rs.getInt(DB.CAT_COL_TYPE));
                    returnItem.setCategory(rs.getString(DB.CAT_COL_CATEGORY));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in categoryFindRecord", e);
        }
        return returnItem;
    }

    /**
     * Reads items from the database using DB.GET_ACTUAL_AND_BUDGET_AMOUNTS,
     * grouping the results by PARENT into a TreeItem<LineItem> tree.
     *
     * @param type The type of items to retrieve.
     * @param date The date to filter the items.
     * @return The root TreeItem containing grouped LineItems by PARENT.
     */
    public static TreeItem<LineItem> getTableAmountsTree(int type, LocalDate date) {
        TreeItem<LineItem> rootNode = new TreeItem<>(new LineItem());
        Map<String, TreeItem<LineItem>> parentMap = new HashMap<>();

        // Use Util.formatDateForDatabase for consistent formatting
        String dateString = Util.formatDateForDatabase(date);

        try (PreparedStatement ps = DataSource.getConn().prepareStatement(DB.GET_ACTUAL_AND_BUDGET_AMOUNTS)) {
            ps.setString(1, dateString);
            ps.setInt(2, type);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    LineItem newItem = createLineItemFromResultSet(rs, type);

                    String parent = newItem.getParent();

                    // Get or create parent node
                    TreeItem<LineItem> parentNode = parentMap.get(parent);
                    if (parentNode == null) {
                        parentNode = new TreeItem<>(newItem);
                        parentMap.put(parent, parentNode);
                        rootNode.getChildren().add(parentNode);
                    }
                    else {
                        // Update existing parent node with new item
                        if (parentNode.getChildren().isEmpty()) {
                            LineItem existingItem = parentNode.getValue();
                            existingItem.setActual(newItem.getActual());
                            existingItem.setBudget(newItem.getBudget());
                        }
                        else {
                            // If the parent already has children, we need to
                            // update
                            // the existing item
                            LineItem existingItem = parentNode.getValue();
                            existingItem.setActual(existingItem.getActual() + newItem.getActual());
                            existingItem.setBudget(existingItem.getBudget() + newItem.getBudget());
                        }
                        parentNode.getChildren().add(new TreeItem<>(newItem));
                    }

                }
            }

            // Calculate root totals
            calculateRootTotals(rootNode);

        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in getTableAmountsTree", e);
        }
        return rootNode;
    }

    /**
     * Retrieves table amounts as a flat list.
     */
    public static List<LineItem> getTableAmounts(int type, LocalDate date) {
        List<LineItem> items = new ArrayList<>();
        String monthString = String.format("%02d", date.getMonthValue());
        String yearString = String.format("%04d", date.getYear());

        try (PreparedStatement ps = DataSource.getConn().prepareStatement(DB.GET_ACTUAL_AND_BUDGET_AMOUNTS)) {
            ps.setString(1, monthString);
            ps.setString(2, yearString);
            ps.setInt(3, type);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LineItem newItem = createLineItemFromResultSet(rs, type);
                    newItem.includeInTotal(rs.getBoolean("INCLUDE_IN_TOTAL"));

                    if (!newItem.hide()) {
                        items.add(newItem);
                    }
                }
            }
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in getTableAmounts", e);
        }
        return items;
    }

    /**
     * Find categories not in actual table for a given month and add to the
     * actual table.
     */
    public static List<LineItemCSV> findMissingCategories(LocalDate date) {
        List<LineItemCSV> items = new ArrayList<>();
    

        String dateString = Util.formatDateForDatabase(date);

        try (PreparedStatement ps = DataSource.getConn().prepareStatement(DB.FIND_MISSING_CATEGORIES)) {
            ps.setString(1, dateString);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LineItemCSV newItem = createLineItemCSVFromResultSet(rs, date);
                    newItem = WriteData.actualInsertRecord(newItem);

                    if (!newItem.hide()) {
                        items.add(newItem);
                    }
                }
            }
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in findMissingCategories", e);
        }
        return items;
    }

    /**
     * Get totals for a specific type and date.
     */
    public static LineItem getTotals(int type, LocalDate date) {
        LineItem newItem = new LineItem();
        String monthString = String.format("%02d", date.getMonthValue());
        String yearString = String.format("%04d", date.getYear());

        try (PreparedStatement ps = DataSource.getConn().prepareStatement(DB.GET_TOTALS)) {
            ps.setString(1, monthString);
            ps.setString(2, yearString);
            ps.setInt(3, type);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    newItem.setActual(rs.getDouble("ATOTAL"));
                    newItem.setBudget(rs.getDouble("BTOTAL"));
                    newItem.setCategory("TOTAL");
                    newItem.setType(type);
                }
            }
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in getTotals", e);
        }
        return newItem;
    }

    /**
     * Get available years from database.
     */
    public static List<String> getYears() {
        List<String> years = new ArrayList<>();

        try (PreparedStatement ps = DataSource.getConn().prepareStatement(DB.ACTUAL_GET_AVAILABLE_YEARS);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String year = rs.getString("YEAR");
                if (year != null) {
                    years.add(year);
                }
            }

            if (years.isEmpty()) {
                years.add(String.valueOf(LocalDate.now().getYear()));
            }
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in getYears", e);
        }
        return years;
    }

    /**
     * Get categories from the database.
     */
    public static List<Categories> getCategories() {
        List<Categories> categories = new ArrayList<>();

        try (PreparedStatement ps = DataSource.getConn().prepareStatement(DB.CAT_GET_CATEGORIES);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categories newItem = createCategoryFromResultSet(rs);
                categories.add(newItem);
            }
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in getCategories", e);
        }
        return categories;
    }

    // Private helper methods
    private static LineItemCSV createCopyWithId(LineItemCSV source, int id) {
        LineItemCSV copy = new LineItemCSV();
        copy.setId(id);
        copy.setAmount(source.getAmount());
        copy.setDate(source.getDate());
        copy.setCategory(source.getCategory());
        copy.setParent(source.getParent());
        copy.setType(source.getType());
        return copy;
    }

    private static LineItem createLineItemFromResultSet(ResultSet rs, int type) throws SQLException {
        LineItem item = new LineItem();
        item.setId(rs.getInt("ID"));
        item.setType(type);
        item.hide(rs.getBoolean("HIDE"));
        String dateStr = rs.getString("DATE") + "-01";
        item.setDate(LocalDate.parse(dateStr));
        item.isCategory(rs.getBoolean("MAIN_CATEGORY"));
        item.setParent(rs.getString("PARENT"));
        item.setCategory(rs.getString("CATEGORY"));
        item.setActual(rs.getDouble("ACTUAL"));
        item.setBudget(rs.getDouble("BUDGET"));
        item.setStartBal(rs.getDouble("STARTBAL"));
        return item;
    }

    private static LineItemCSV createLineItemCSVFromResultSet(ResultSet rs, LocalDate date) throws SQLException {
        LineItemCSV item = new LineItemCSV();
        item.setId(rs.getInt("ID"));
        item.setDate(date);
        item.setType(rs.getInt("TYPE"));
        item.setParent(rs.getString("PARENT"));
        item.setCategory(rs.getString("CATEGORY"));
        return item;
    }

    private static Categories createCategoryFromResultSet(ResultSet rs) throws SQLException {
        Categories category = new Categories();
        category.setId(rs.getInt("ID"));
        category.setIncludeInTotal(rs.getBoolean("INCLUDE_IN_TOTAL"));
        category.hide(rs.getBoolean("HIDE"));
        category.setType(rs.getInt("TYPE"));
        category.setParent(rs.getString("PARENT"));
        category.setCategory(rs.getString("CATEGORY"));
        return category;
    }


    private static void calculateRootTotals(TreeItem<LineItem> rootNode) {
        LineItem rootItem = rootNode.getValue();
        double rootActualTotal = 0.0;
        double rootBudgetTotal = 0.0;

        for (TreeItem<LineItem> parentNode : rootNode.getChildren()) {
            LineItem parentItem = parentNode.getValue();
            rootActualTotal += parentItem.getActual();
            rootBudgetTotal += parentItem.getBudget();
        }

        rootItem.setActual(rootActualTotal);
        rootItem.setBudget(rootBudgetTotal);
    }

    // Add to your ReadData class

    /**
     * Gets all running totals for a specific month, using effective totals.
     */
    public static List<RunningTotal> getRunningTotalsForMonth(LocalDate monthDate) {
        List<RunningTotal> totals = new ArrayList<>();

        if (!DataSource.getInstance().ensureConnection()) {
            return totals;
        }

        try (PreparedStatement stmt = DataSource.getConn().prepareStatement(DB.RUNNING_TOTAL_GET_ALL_FOR_MONTH_WITH_MODIFIED)) {
            stmt.setString(1, Util.formatDateForDatabase(monthDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RunningTotal total = new RunningTotal();
                    total.setId(rs.getInt("id"));
                    total.setCategoryId(rs.getInt("category_id"));
                    total.setCategoryName(rs.getString("category"));
                    total.setMonthDateFromString(rs.getString("month_date"));
                    total.setPreviousBalance(rs.getDouble("previous_balance"));
                    total.setCurrentDifference(rs.getDouble("current_difference"));
                    total.setRunningTotal(rs.getDouble("running_total"));
                    total.setModifiedRunningTotal(rs.getObject("modified_running_total", Double.class));
                    total.setMaximumAmount(rs.getObject("maximum_amount", Double.class));
                    total.setWarningIssued(rs.getBoolean("warning_issued"));

                    totals.add(total);
                }
            }
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting running totals for month", e);
        }

        return totals;
    }

    /**
     * Gets categories with negative effective running totals for a specific month.
     */
    public static List<RunningTotal> getNegativeRunningTotals(LocalDate monthDate) {
        List<RunningTotal> negatives = new ArrayList<>();

        if (!DataSource.getInstance().ensureConnection()) {
            return negatives;
        }

        try (PreparedStatement stmt = DataSource.getConn().prepareStatement(DB.RUNNING_TOTAL_GET_NEGATIVE_TOTALS_WITH_MODIFIED)) {
            stmt.setString(1, Util.formatDateForDatabase(monthDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RunningTotal total = new RunningTotal();
                    total.setId(rs.getInt("id"));
                    total.setCategoryId(rs.getInt("category_id"));
                    total.setCategoryName(rs.getString("category"));
                    total.setMonthDateFromString(rs.getString("month_date"));
                    total.setPreviousBalance(rs.getDouble("previous_balance"));
                    total.setCurrentDifference(rs.getDouble("current_difference"));
                    total.setRunningTotal(rs.getDouble("running_total"));
                    total.setModifiedRunningTotal(rs.getObject("modified_running_total", Double.class));
                    total.setMaximumAmount(rs.getObject("maximum_amount", Double.class));
                    total.setWarningIssued(rs.getBoolean("warning_issued"));

                    negatives.add(total);
                }
            }
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting negative running totals", e);
        }

        return negatives;
    }
}
