package com.example.data;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Test;

public class LineItemTest {

    @Test
    public void testLineItemConstructor() {
        int type = 1;
        LocalDate date = LocalDate.of(2022, 1, 1);
        String parent = "Parent";
        String category = "Category";
        double actual = 100.0;
        double budget = 200.0;
        double diff = budget - actual;

        LineItem lineItem = new LineItem(type, date, parent, category, actual, budget);
        System.out.println(lineItem);

        assertEquals(type, lineItem.getType());
        assertEquals(date, lineItem.getDate());
        assertEquals(parent, lineItem.getParent());
        assertEquals(category, lineItem.getCategory());
        assertEquals(actual, lineItem.getActual(), 0.01);
        assertEquals(budget, lineItem.getBudget(), 0.01);
        assertEquals(diff, lineItem.getDiff(), 0.01);
    }

    @Test
    public void testLineItemSettersAndGetters() {
        LineItem lineItem = new LineItem();

        int id = 1;
        lineItem.setId(id);
        assertEquals(id, lineItem.getId());

        int type = 2;
        lineItem.setType(type);
        assertEquals(type, lineItem.getType());

        LocalDate date = LocalDate.of(2022, 2, 2);
        lineItem.setDate(date);
        assertEquals(date, lineItem.getDate());

        String parent = "Parent";
        lineItem.setParent(parent);
        assertEquals(parent, lineItem.getParent());

        String category = "Category";
        lineItem.setCategory(category);
        assertEquals(category, lineItem.getCategory());

        double actual = 300.0;
        lineItem.setActual(actual);
        assertEquals(actual, lineItem.getActual(), 0.01);

        double budget = 400.0;
        lineItem.setBudget(budget);
        assertEquals(budget, lineItem.getBudget(), 0.01);

        double diff = budget - actual;
        lineItem.setDiff(diff);
        assertEquals(diff, lineItem.getDiff(), 0.01);
    }

}
