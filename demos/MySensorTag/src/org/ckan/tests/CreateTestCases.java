package org.ckan.tests;


import org.junit.* ;
import static org.junit.Assert.* ;

import org.ckan.*;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.UUID;

public class CreateTestCases {

    private static String ApiKey;
    static {
        ApiKey = System.getenv("APIKEY");
        if ( ApiKey == null ) {
            throw new RuntimeException("Unable to find APIKEY env variable");
        }
    }

    @Test
    public void test_CreateDataset() {
        System.out.println("Test 1");
        Client c = new Client( new Connection("http://localhost", 5000),
                              CreateTestCases.ApiKey);
        try {
            Dataset ds = new Dataset();
            ds.setName( UUID.randomUUID().toString() );
            ds.setTitle("Test Dataset");
            ds.setNotes("A description");

            List<Extra> extras = new ArrayList<Extra>();
            extras.add( new Extra("Extra Field", "\"Extra Value\"") );
            ds.setExtras(extras);

            Dataset result = c.createDataset(ds);
            //assertEquals( result.getExtras().size(), 1 );
            //System.out.println( result.getExtras() );
        } catch ( CKANException cke ) {
            System.out.println(cke);
        } catch ( Exception e ) {
            System.out.println(e);
            assertEquals( 1, 0);
        }
        System.out.println("Test 1 end");
    }


    @Test
    public void test_CreateDatasetWithPlusInName() {
        System.out.println("Test 2");
        Client c = new Client( new Connection("http://localhost", 5000),
                              CreateTestCases.ApiKey);
        try {
            Dataset ds = new Dataset();
            ds.setName( UUID.randomUUID().toString() );
            ds.setTitle("Test + Dataset");
            ds.setNotes("A description & some notes");

            Dataset result = c.createDataset(ds);
            System.out.println( result );
            System.out.println( result.getNotes() );
        } catch ( CKANException e ) {
            System.out.println(e);
        }
        System.out.println("Test 2 end");
    }

}


