package com.example.controllers;

import java.io.File;
import java.time.LocalDate;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.data.DataSource;

import javafx.application.Platform;

public class PrimaryControllerTest {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        if (!DataSource.getInstance().open()) {
            System.out.println("FATAL ERROR: Couldn't connect to database");
            Platform.exit();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        DataSource.getInstance().close();
    }

    @Test
    public void testReadActual() {
 
        File csvfile = new File("G:\\budget\\budgetPrint.csv");

        PrimaryController.readActual(csvfile, LocalDate.now());
    }

    
}
