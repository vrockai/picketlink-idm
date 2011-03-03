/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package org.picketlink.idm.performance.concurent.cases;

import org.picketlink.idm.performance.concurent.ConcurentDBTestBase;
import org.picketlink.idm.performance.concurent.worker.InsertDBWorker;
import org.junit.Test;
import org.picketlink.idm.ldap.cases.LDAPIdentityStoreTests;
import org.picketlink.idm.performance.concurent.worker.LDAPWorker;
import org.picketlink.idm.util.PropertiesSingelton;

/**
*
* @author vrockai
*/
public class ConcurentIDMLDAPTestCase extends ConcurentDBTestBase {

    int WORKER_NUM = Integer.valueOf(PropertiesSingelton.getInstance().getProperties().getProperty("worker.number"));
    int JOB_NUM = Integer.valueOf(PropertiesSingelton.getInstance().getProperties().getProperty("worker.job.number"));
    LDAPIdentityStoreTests LDAPtests = new LDAPIdentityStoreTests();

    @Test
    public void testRELATIONSHIPS() throws Exception {
        //System.out.println("testRELATIONSHIPS");
        LDAPtests.setUp();
        //System.out.println("1");
        LDAPtests.populateClean();
        //System.out.println("2");
        LDAPtests.begin();
        //System.out.println("3");
        populateWorkers(WORKER_NUM, JOB_NUM, LDAPWorker.TestMethod.RELATIONSHIPS);
        timeStart = System.currentTimeMillis();
        //System.out.println("4");
        runWorkers();
        timeEnd = System.currentTimeMillis();
        //System.out.println("5");
        checkExceptions();
        //System.out.println("6");
        LDAPtests.tearDown();
    }

    @Test
    public void testCREATE_CRED() throws Exception {
        //System.out.println("testCREATE_CRED");
        LDAPtests.setUp();
        //System.out.println("1");
        LDAPtests.populateClean();
        //System.out.println("2");
        LDAPtests.begin();
        //System.out.println("3");
        populateWorkers(WORKER_NUM, JOB_NUM, LDAPWorker.TestMethod.CREATE_CRED);
        timeStart = System.currentTimeMillis();
        //System.out.println("4");
        runWorkers();
        timeEnd = System.currentTimeMillis();
        //System.out.println("5");
        checkExceptions();
        //System.out.println("6");
        LDAPtests.tearDown();
    }

    @Test
    public void testCREATE_GPR() throws Exception {
        //System.out.println("testCREATE_GPR");
        LDAPtests.setUp();
        //System.out.println("1");
        LDAPtests.populateClean();
        //System.out.println("2");
        LDAPtests.begin();
        //System.out.println("3");
        populateWorkers(WORKER_NUM, JOB_NUM, LDAPWorker.TestMethod.CREATE_GPR);
        timeStart = System.currentTimeMillis();
        //System.out.println("4");
        runWorkers();
        timeEnd = System.currentTimeMillis();
        //System.out.println("5");
        checkExceptions();
        //System.out.println("6");
        LDAPtests.tearDown();
    }


    @Test
    public void testCREATE_USR() throws Exception {
        //System.out.println("testCREATE_USR");
        LDAPtests.setUp();
        //System.out.println("1");
        LDAPtests.populateClean();
        //System.out.println("2");
        LDAPtests.begin();
        //System.out.println("3");
        populateWorkers(WORKER_NUM, JOB_NUM, LDAPWorker.TestMethod.CREATE_USR);
        timeStart = System.currentTimeMillis();
        //System.out.println("4");
        runWorkers();
        timeEnd = System.currentTimeMillis();
        //System.out.println("5");
        checkExceptions();
        //System.out.println("6");
        LDAPtests.tearDown();
    }

    @Test
    public void testCRITERIA() throws Exception {
        //System.out.println("testCRITERIA");
        LDAPtests.setUp();
        //System.out.println("1");
        LDAPtests.populateClean();
        //System.out.println("2");
        LDAPtests.begin();
        //System.out.println("3");
        populateWorkers(WORKER_NUM, JOB_NUM, LDAPWorker.TestMethod.CRITERIA);
        timeStart = System.currentTimeMillis();
        //System.out.println("4");
        runWorkers();
        timeEnd = System.currentTimeMillis();
        //System.out.println("5");
        checkExceptions();
        //System.out.println("6");
        LDAPtests.tearDown();
    }

    @Test
    public void testFIND_IDENTITY() throws Exception {
        //System.out.println("testFIND_IDENTITY");
        LDAPtests.setUp();
        //System.out.println("1");
        LDAPtests.populateClean();
        //System.out.println("2");
        LDAPtests.begin();
        //System.out.println("3");
        populateWorkers(WORKER_NUM, JOB_NUM, LDAPWorker.TestMethod.FIND_IDENTITY);
        timeStart = System.currentTimeMillis();
        //System.out.println("4");
        runWorkers();
        timeEnd = System.currentTimeMillis();
        //System.out.println("5");
        checkExceptions();
        //System.out.println("6");
        LDAPtests.tearDown();
    }

    @Test
    public void testREMOVE_GRP() throws Exception {
        //System.out.println("testREMOVE_GRP");
        LDAPtests.setUp();
        //System.out.println("1");
        LDAPtests.populateClean();
        //System.out.println("2");
        LDAPtests.begin();
        //System.out.println("3");
        populateWorkers(WORKER_NUM, JOB_NUM, LDAPWorker.TestMethod.REMOVE_GRP);
        timeStart = System.currentTimeMillis();
        //System.out.println("4");
        runWorkers();
        timeEnd = System.currentTimeMillis();
        //System.out.println("5");
        checkExceptions();
        //System.out.println("6");
        LDAPtests.tearDown();
    }

    @Test
    public void testREMOVE_USR() throws Exception {
        //System.out.println("testREMOVE_USR");
        LDAPtests.setUp();
        //System.out.println("1");
        LDAPtests.populateClean();
        //System.out.println("2");
        LDAPtests.begin();
        //System.out.println("3");
        populateWorkers(WORKER_NUM, JOB_NUM, LDAPWorker.TestMethod.REMOVE_USR);
        timeStart = System.currentTimeMillis();
        //System.out.println("4");
        runWorkers();
        timeEnd = System.currentTimeMillis();
        //System.out.println("5");
        checkExceptions();
        //System.out.println("6");
        LDAPtests.tearDown();
    }

    public void populateWorkers(int size, int op_number, LDAPWorker.TestMethod testMethod) throws Exception {

        for (int i = 0; i < size; i++) {

            workers.add(new LDAPWorker(LDAPtests, op_number, testMethod));
        }

    }

}
