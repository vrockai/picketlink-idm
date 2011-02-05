/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.performance.concurent.cases;

import org.picketlink.idm.performance.concurent.ConcurentDBTestBase;
import org.picketlink.idm.performance.concurent.worker.InsertDBWorker;
import org.junit.Test;
import org.picketlink.idm.util.PropertiesSingelton;

/**
 *
 * @author vrockai
 */
public class ConcurentIDMMethodsTestCase extends ConcurentDBTestBase {

    int WORKER_NUM = Integer.valueOf(PropertiesSingelton.getInstance().getProperties().getProperty("worker.number"));
    int JOB_NUM = Integer.valueOf(PropertiesSingelton.getInstance().getProperties().getProperty("worker.job.number"));

    @Test
    public void testConcurentAccess() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, InsertDBWorker.TestMethod.CREATE_USERS);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testAssociateRoles() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, InsertDBWorker.TestMethod.ASSOCIATE_ROLES);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testAssociateUsers() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, InsertDBWorker.TestMethod.ASSOCIATE_USERS);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testCreateGroups() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, InsertDBWorker.TestMethod.CREATE_GROUPS);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testCreateRoleTypes() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, InsertDBWorker.TestMethod.CREATE_ROLE_TYPES);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testDisassociateUsers() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, InsertDBWorker.TestMethod.DISSASSOCIATE_USERS);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testRemoveGroups() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, InsertDBWorker.TestMethod.REMOVE_GROUPS);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testRemoveRoles() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, InsertDBWorker.TestMethod.REMOVE_ROLES);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testRemoveRoleTypes() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, InsertDBWorker.TestMethod.REMOVE_ROLES_TYPES);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testRemoveUsers() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, InsertDBWorker.TestMethod.REMOVE_USERS);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    public void populateWorkers(int size, int op_number, InsertDBWorker.TestMethod testMethod) {
        for (int i = 0; i < size; i++) {
            workers.add(new InsertDBWorker(identitySessionFactory, op_number, testMethod));
        }
    }
}
