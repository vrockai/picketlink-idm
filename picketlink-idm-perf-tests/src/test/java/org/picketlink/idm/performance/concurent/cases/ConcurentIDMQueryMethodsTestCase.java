/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.performance.concurent.cases;

import org.picketlink.idm.performance.concurent.ConcurentDBTestBase;
import org.picketlink.idm.performance.concurent.worker.InsertDBWorker;
import org.junit.Test;
import org.picketlink.idm.performance.concurent.worker.QueryDBWorker;
import org.picketlink.idm.util.PropertiesSingelton;

/**
 *
 * @author vrockai
 */
public class ConcurentIDMQueryMethodsTestCase extends ConcurentDBTestBase {

    int WORKER_NUM = Integer.valueOf(PropertiesSingelton.getInstance().getProperties().getProperty("worker.number"));
    int JOB_NUM = Integer.valueOf(PropertiesSingelton.getInstance().getProperties().getProperty("worker.job.number"));

     @Test
    public void testFIND_GROUP() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, QueryDBWorker.TestMethod.FIND_GROUP);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testFIND_GROUP_ROLETYPES() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, QueryDBWorker.TestMethod.FIND_GROUP_ROLETYPES);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testFIND_GROUP_WITH_RELATED_ROLE() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, QueryDBWorker.TestMethod.FIND_GROUP_WITH_RELATED_ROLE);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testFIND_ROLES() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, QueryDBWorker.TestMethod.FIND_ROLES);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testFIND_ROLE_TYPES() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, QueryDBWorker.TestMethod.FIND_ROLE_TYPES);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testFIND_USER() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, QueryDBWorker.TestMethod.FIND_USER);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    @Test
    public void testFIND_USER_ROLETYPES() throws Exception {
        populateWorkers(WORKER_NUM, JOB_NUM, QueryDBWorker.TestMethod.FIND_USER_ROLETYPES);
        timeStart = System.currentTimeMillis();
        runWorkers();
        timeEnd = System.currentTimeMillis();
        checkExceptions();
    }

    public void populateWorkers(int size, int op_number, QueryDBWorker.TestMethod testMethod) {
        for (int i = 0; i < size; i++) {
            workers.add(new QueryDBWorker(identitySessionFactory, op_number, testMethod));
        }
    }
}
