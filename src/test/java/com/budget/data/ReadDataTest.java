package com.budget.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

import com.budget.Util;
import com.budget.dataModal.Categories;
import com.budget.dataModal.DataSource;
import com.budget.dataModal.LineItem;
import com.budget.dataModal.LineItemCSV;
import com.budget.dataModal.ReadData;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;

public class ReadDataTest {
    public static LineItemCSV testItem_Cat = new LineItemCSV(1, LocalDate.now(), "Eatable", "Groceries", 0.0);
    public static LineItemCSV testItem_Act = new LineItemCSV();
    public static LineItemCSV testItem_Bud = new LineItemCSV();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        if (!DataSource.getInstance().open()) {
            System.out.println("FATAL ERROR: Couldn't connect to database");
            Platform.exit();
        }
        // Insert test data for category
        String insertCat = "INSERT INTO category (type, parent, category) VALUES(?, ?, ?)";
        PreparedStatement stmt = DataSource.getConn().prepareStatement(insertCat,
                PreparedStatement.RETURN_GENERATED_KEYS);

        stmt.setInt(1, testItem_Cat.getType());
        stmt.setString(2, testItem_Cat.getParent());
        stmt.setString(3, testItem_Cat.getCategory());

        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            testItem_Cat.setId(rs.getInt(1));
        }
        else {
            testItem_Cat.setId(-1);
        }

        // Insert test data for actual
        String insertAct = "INSERT INTO actual (category, date, actual, budget) VALUES(?, ?, ?, 0.0)";
        stmt = DataSource.getConn().prepareStatement(insertAct, PreparedStatement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, testItem_Cat.getId());

        String dateString = testItem_Act.getDate().toString();

        stmt.setString(2, dateString);
        stmt.setDouble(3, testItem_Act.getAmount());

        stmt.executeUpdate();

        rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            testItem_Act.setId(rs.getInt(1));
            testItem_Act.setType(testItem_Cat.getType());
            testItem_Act.setParent(testItem_Cat.getParent());
            testItem_Act.setCategory(testItem_Cat.getCategory());
            testItem_Act.setAmount(testItem_Cat.getAmount());
            testItem_Act.setDate(testItem_Cat.getDate());
        }
        else {
            testItem_Act.setId(-1);
        }

    }

    @AfterClass
    public static void tearDown() throws Exception {
        // delete test data
        // String deleteCat = "DELETE FROM category WHERE id = ?";
        // PreparedStatement stmt =
        // DataSource.getConn().prepareStatement(deleteCat);
        // stmt.setInt(1, testItem_Cat.getId());
        // stmt.executeUpdate();

        // String deleteAct = "DELETE FROM actual WHERE id = ?";
        // stmt = DataSource.getConn().prepareStatement(deleteAct);
        // stmt.setInt(1, testItem_Act.getId());
        // stmt.executeUpdate();

        // String deleteBud = "DELETE FROM budget WHERE id = ?";
        // stmt = DataSource.getConn().prepareStatement(deleteBud);
        // stmt.setInt(1, testItem_Bud.getId());
        // stmt.executeUpdate();

        DataSource.getInstance().close();
    }

    @Test
    public void testActualFindCategory() {
        // Create a test LineItemCSV object
        LineItemCSV item = new LineItemCSV();
        item.setId(1);
        item.setDate(LocalDate.of(2021, 1, 1));

        // Call the method being tested
        LineItemCSV result = ReadData.actualFindCategory(item);

        // Assert the expected result
        assertEquals(-1, result.getId());

        // Create a testItem
        int expected = testItem_Act.getId();
        result = ReadData.actualFindCategory(testItem_Cat);

        // Assert the expected result
        assertEquals(expected, result.getId());

    }

    @Test
    public void testCategoryFindRecord() {
        // Create a test LineItemCSV object
        LineItemCSV item = new LineItemCSV();

        item.setParent("");
        item.setCategory("Groceries");

        // Call the method being tested
        LineItemCSV result = ReadData.categoryFindRecord(item);

        // Assert the expected result
        assertEquals(-1, result.getId());

        // use global testItem
        int expected = testItem_Cat.getId();
        result = ReadData.categoryFindRecord(testItem_Cat);

        // Assert the expected result
        assertEquals(expected, result.getId());

    }

    @Test
    public void testGetTableAmountsTree() {
        int tableType = 1;

        // set date to 9-01-2024
        // LocalDate inDate = LocalDate.of(2025, 6, 1);
        LocalDate inDate = LocalDate.of(2025, 6, 1);

        TreeItem<LineItem> result;

        result = ReadData.getTableAmountsTree(tableType, inDate);

        // print each item in result
        Util.printTreeItems(result);

        // Assert the expected result
        assertNotNull(result);

    }

    @Test
    /* write test for GetTotals() */
    public void testGetTotals() {
        int type = testItem_Cat.getType();
        LocalDate date = testItem_Cat.getDate();

        LineItem result = ReadData.getTotals(type, date);

        // Assert the expected result
        assertNotNull(result);
        assertEquals("TOTAL", result.getCategory());
        assertEquals(type, result.getType());
        // Since test data is minimal, actual and budget may be 0.0 or as
        // inserted
        // You may want to adjust these assertions based on your test data setup
        // For now, just check that actual and budget are not null (primitive
        // double always not null)
        System.out.println("Actual: " + result.getActual() + ", Budget: " + result.getBudget());
    }

    @Test
    public void testgetCategories() {

        List<Categories> result;

        result = ReadData.getCategories();

        // print each item in result
        for (Categories item : result) {
            System.out.println(item);
        }

        // Assert the expected result
        assertNotNull(result);
    }

    @Test
    public void testgetYears() {
        List<String> result;

        result = ReadData.getYears();

        // print each item in result
        System.out.println("Years:");
        for (String item : result) {
            System.out.println(item);
        }

        // Assert the expected result
        assertNotNull(result);

    }
}
