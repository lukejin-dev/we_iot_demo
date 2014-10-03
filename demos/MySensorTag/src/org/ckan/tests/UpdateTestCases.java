package org.ckan.tests;


import org.junit.* ;
import static org.junit.Assert.* ;

import org.ckan.*;

import com.google.gson.Gson;



public class UpdateTestCases {
    private static String ApiKey;
    static {
        ApiKey = System.getenv("APIKEY");
        if ( ApiKey == null ) {
            throw new RuntimeException("Unable to find APIKEY env variable");
        }
    }

    @Test
    public void test_UpdateDataset() {
/*        Client c = new Client( new Connection("http://localhost", 5000),
                                 UpdateTestCases.ApiKey);
        try {
            Dataset ds = c.getDataset( "BjFHjVHqZ73BVquXPwk0lw" );
            System.out.println( ds );
        } catch ( CKANException e ) {
            System.out.println(e);
        }*/
    }


}


