package com.example.data;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Test;

public class LineItemTest {

    @Test
    public void testLineItemConstructor() {
        int type = 0;
        Boolean hide = false;
        LocalDate date = LocalDate.of(2022, 1, 1);
        String parent = "Parent";
        String category = "Category";
        double actual = 100.0;
        double budget = 200.0;
        double diff = 0.0;
        if (type != 0) {
            diff = actual - budget;
        }
        else {
            diff = budget - actual;
        }

        LineItem lineItem = new LineItem(type, date, parent, category, actual, budget);
        System.out.println(lineItem);

        System.out.println(lineItem.getType());
        System.out.println(lineItem.getActual());
        System.out.println(lineItem.getBudget());
        System.out.println(lineItem.getDiff());

        assertEquals(type, lineItem.getType());
        assertEquals(hide, lineItem.getHide());
        assertEquals(date, lineItem.getDate());
        assertEquals(parent, lineItem.getParent());
        assertEquals(category, lineItem.getCategory());
        assertEquals(actual, lineItem.getActual(), 0.01);
        assertEquals(budget, lineItem.getBudget(), 0.01);
        assertEquals(diff, lineItem.getDiff(), 0.01);

        type = 1;
        if (type != 0) {
            diff = actual - budget;
        }
        else {
            diff = budget - actual;
        }

        LineItem lineItem2 = new LineItem(type, date, parent, category, actual, budget);
        System.out.println(lineItem);

        System.out.println(lineItem2.getType());
        System.out.println(lineItem2.getActual());
        System.out.println(lineItem2.getBudget());
        System.out.println(lineItem2.getDiff());

        assertEquals(type, lineItem2.getType());
        assertEquals(hide, lineItem2.getHide());
        assertEquals(date, lineItem2.getDate());
        assertEquals(parent, lineItem2.getParent());
        assertEquals(category, lineItem2.getCategory());
        assertEquals(actual, lineItem2.getActual(), 0.01);
        assertEquals(budget, lineItem2.getBudget(), 0.01);
        assertEquals(diff, lineItem2.getDiff(), 0.01);

    }

    @Test
    public void testLineItemSettersAndGetters() {
        LineItem lineItem = new LineItem();

        int id = 1;
        lineItem.setId(id);
        assertEquals(id, lineItem.getId());

        Boolean hide = true;
        lineItem.setHide(hide);
        assertEquals(hide, lineItem.getHide());

        int type = 2;
        lineItem.setType(type);
        assertEquals(type, lineItem.getType());

        LocalDate date = LocalDate.of(2022, 2, 2);
        lineItem.setDate(date);
        assertEquals(date, lineItem.getDate());

        String parent = "Parent";
        lineItem.setParent(parent);
        assertEquals(parent, lineItem.getParent());
        /***************************************************************************** */
        String category = "Category";
        lineItem.setCategory(category);
        lineItem.setIsCategory(false);
        System.out.println("Category: " + lineItem.getCategory());
       // assertEquals(category, lineItem.getCategory());

        double actual = 300.0;
        lineItem.setActual(actual);
        assertEquals(actual, lineItem.getActual(), 0.01);

        double budget = 400.0;
        lineItem.setBudget(budget);
        assertEquals(budget, lineItem.getBudget(), 0.01);

        lineItem.setType(0);
        System.out.println("Diff: " + lineItem.getDiff());
        lineItem.setType(1);
        System.out.println("Diff: " + lineItem.getDiff());
    }
}
