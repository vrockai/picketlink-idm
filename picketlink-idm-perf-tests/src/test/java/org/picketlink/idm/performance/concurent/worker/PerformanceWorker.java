/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.performance.concurent.worker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.picketlink.idm.api.IdentitySessionFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author vrockai
 */
public abstract class PerformanceWorker extends Thread {

    protected long timeStart;
    protected long timeEnd;

    private Map<Integer, Number> measure;
    protected long starttime;
    protected long stoptime;
    boolean isGraph = true;
    boolean isFileLog = false;
    protected long test_id;
    protected String graphBaseDir = "graphs";

    protected int USER_NUM = 1000;
    protected boolean triggerGc = false;
    protected IdentitySessionFactory identitySessionFactory;
    protected int worker_id = 0;
    protected static int worker_count = 0;
    protected Collection<Exception> eList = null;

    protected String graphDir = "graphs/threaded";

    public Collection<Exception> getExceptionList() {
        return eList;
    }
    public long getTotalTime(){
        return timeEnd - timeStart;
    }

    public double getAvgTime(){
        return (timeEnd - timeStart)/(double)(USER_NUM);
    }

    public double getOperationAvgTime(){
        long sum = 0;

        for (Map.Entry<Integer, Number> entry : measure.entrySet()) {            
            sum += (Long) entry.getValue();
        }

        return ((double)sum / measure.size());
    }

    private synchronized void setId(){
        worker_id = worker_count++;
    }

    public PerformanceWorker(IdentitySessionFactory identitySessionFactory) {
        
        setId();
        this.identitySessionFactory = identitySessionFactory;
        
    }

    public PerformanceWorker(int size) {
        setId();
      //  System.out.println("size:"+size);
        USER_NUM = size;
    }

    public PerformanceWorker(IdentitySessionFactory identitySessionFactory, int size) {
        setId();
        USER_NUM = size;
        this.identitySessionFactory = identitySessionFactory;
    }

    protected void resetMeasure() {
        measure = new HashMap<Integer, Number>();
    }

    protected void startStopwatch() {
        starttime = System.currentTimeMillis();// (new Date()).getTime();
    }

    protected void stopStopwatch() {
        stoptime = System.currentTimeMillis();// (new Date()).getTime();
    }

    protected long storeStopwatch(int i) {

        long result = stoptime - starttime;
        if (isGraph) {
            measure.put(i, result);
        }

        if (isFileLog) {
            try {
                FileWriter fw = new FileWriter(new File(test_id + ".log"), true);
                PrintWriter pw = new PrintWriter(fw);
                pw.println(i + "," + result);
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    //TODO move to Graph class
    protected void generateGraph(String filename, String subDir) {

                (new File(graphBaseDir + "/" + subDir)).mkdirs();

		XYSeries series = new XYSeries("XYGraph");

		long sum = 0;

		for (Map.Entry<Integer, Number> entry : measure.entrySet()) {
			series.add(entry.getKey(), entry.getValue());
			sum += (Long) entry.getValue();
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		JFreeChart chart = ChartFactory.createXYLineChart(filename, // Title
				"# of users", // x-axis Label
				"time to create one" + " ... S:" + sum + ", " + "A: " + (sum / measure.size()), // y-axis
				// Label
				dataset, // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				false, // Show Legend
				false, // Use tooltips
				false // Configure chart to generate URLs?
				);
		try {
			ChartUtilities.saveChartAsPNG(new File(graphBaseDir+"/" + subDir +"/" + filename + ".png"), chart, 1200, 800);
		} catch (IOException e) {
                        System.err.println(e);
			System.err.println("Problem occurred creating chart.");
		}

	}

    //TODO move to Graph class
        protected String getGraphNamePfx(){            
            return this.getClass().getName();
        }
}
