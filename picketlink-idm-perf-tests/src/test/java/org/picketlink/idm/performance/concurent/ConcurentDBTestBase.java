/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.performance.concurent;

import java.util.ArrayList;
import java.util.Collection;
import org.picketlink.idm.performance.DBTestBase;
import org.picketlink.idm.performance.concurent.worker.PerformanceWorker;

/**
 *
 * @author vrockai
 */
public class ConcurentDBTestBase extends DBTestBase {

    protected Collection<PerformanceWorker> workers = new ArrayList<PerformanceWorker>();
    protected long timeStart;
    protected long timeEnd;
    
    protected void runWorkers(){
        for (PerformanceWorker worker : workers) {            
            worker.start();
        }

        boolean running = false;
        do{
            running = false;
            for (PerformanceWorker worker : workers) {
                running = !running ? worker.isAlive() : true;
            }
            Thread.yield();
        }while(running);
    }

    protected void printResults() {
        for (PerformanceWorker worker : workers) {
            System.out.println(worker.getTotalTime() + "," + worker.getAvgTime() + "," + worker.getOperationAvgTime());
        }
    }

    protected void checkExceptions() throws Exception {

        boolean exception = false;

        for (PerformanceWorker worker : workers) {
            Collection<Exception> eList = worker.getExceptionList();
            if ( eList != null){
                exception = true;
                for (Exception e : eList){
                    e.printStackTrace();
                }
            }
        }

        if (exception)
            throw new Exception("At least one of the workers throwed an exception.");

    }
}
