/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.performance;

import java.io.File;
import org.picketlink.idm.impl.configuration.IdentityConfigurationImpl;
import org.junit.After;
import org.junit.Before;
import org.picketlink.idm.util.PropertiesSingelton;

/**
 *
 * @author vrockai
 */
public class DBTestBase extends TestBase {

     protected static int USER_NUM = Integer.valueOf(PropertiesSingelton.getInstance().getProperties().getProperty("single.job.number"));;

    @Before
    public void setUp() throws Exception {
        if (identitySessionFactory == null) {
            identitySessionFactory = new IdentityConfigurationImpl().configure(new File("src/test/resources/example-db-config.xml")).buildIdentitySessionFactory();            
        } else if (identitySessionFactory.isClosed()){
            identitySessionFactory = new IdentityConfigurationImpl().configure(new File("src/test/resources/example-db-config.xml")).buildIdentitySessionFactory();            
        }

        test_id = System.currentTimeMillis();
    }

    @After
    public void tearDown() throws Exception {
        if (identitySessionFactory != null) {
            identitySessionFactory.close();            
        }
    }
}
