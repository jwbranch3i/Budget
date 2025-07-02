package com.budget.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.budget.dataModal.Categories;

public class CategoriesTest {
    @Test
    public void testDefaultConstructor() {
        Categories category = new Categories();
        assertEquals(0, category.getId());
        assertFalse(category.hide());
        assertTrue(category.include_in_total());
        assertEquals(0, category.getType());
        assertEquals("", category.getParent());
        assertEquals("", category.getCategory());
        assertEquals(0, category.getAcct());
    }

    @Test
    public void testParameterizedConstructor() {
        Categories category = new Categories(1, "Parent", "Category");
        assertEquals(1, category.getType());
        assertEquals("Parent", category.getParent());
        assertEquals("Category", category.getCategory());
        assertTrue(Categories.getItems().contains("Income"));
        assertTrue(Categories.getItems().contains("Manditory"));
        assertTrue(Categories.getItems().contains("Discretionary"));
    }

    @Test
    public void testSettersAndGetters() {
        Categories category = new Categories();
        category.setId(1);
        category.hide(true);
        category.include_in_total(false);
        category.setType(2);
        category.setParent("New Parent");
        category.setCategory("New Category");
        category.setAcct(3);

        assertEquals(1, category.getId());
        assertTrue(category.hide());
        assertFalse(category.include_in_total());
        assertEquals(2, category.getType());
        assertEquals("New Parent", category.getParent());
        assertEquals("New Category", category.getCategory());
        assertEquals(3, category.getAcct());
    }

}
