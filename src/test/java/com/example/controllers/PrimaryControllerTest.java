package com.example.controllers;

import org.junit.Test;

public class PrimaryControllerTest {

 @Test
 public void testReadActual(){
        PrimaryController primaryController = new PrimaryController();
        primaryController.readActual();
        
        assertEquals(1, primaryController.readActual());


 }   

}
