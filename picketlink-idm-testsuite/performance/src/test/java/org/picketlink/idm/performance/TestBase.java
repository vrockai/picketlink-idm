package org.picketlink.idm.performance;

/*
 * JBoss, a division of Red Hat
 * Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
 
import org.opends.server.tools.LDAPModify;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.Binding;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.InitialLdapContext;
import junit.framework.TestCase;
import org.picketlink.idm.api.IdentitySessionFactory;
import org.picketlink.idm.test.support.hibernate.HibernateTestSupport;
import org.picketlink.idm.test.support.opends.OpenDSService;

/**
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw
 *         Dawidowicz</a>
 * @version : 0.1 $
 */
public class TestBase extends TestCase {

  public static IdentitySessionFactory identitySessionFactory;
        
  protected String graphBaseDir = "graphs";

	protected static Logger logger = Logger.getLogger(TestBase.class.getName());

   protected long test_id;

   private OpenDSService openDSService;

   //TODO here resolve hibernate name and config file from properties
   HibernateTestSupport hibernateTestSupport = new HibernateTestSupport("perf_test", "hibernate-jboss-identity.cfg.xml");


	boolean isGraph = true;
	boolean isFileLog = false;

	public static final String LDAP_HOST = "localhost";
	public static final String LDAP_PORT = "10389";
	public static final String LDAP_PROVIDER_URL = "ldap://" + LDAP_HOST + ":" + LDAP_PORT;
	public static final String LDAP_PRINCIPAL = "cn=Directory Manager";
	public static final String LDAP_CREDENTIALS = "password";

   protected void startDatabase() throws Exception
   {
      hibernateTestSupport.start();

	}

	protected void stopDatabase() throws Exception
   {
      try
      {
         hibernateTestSupport.stop();
      }
      catch (Throwable ex)
      {
         logger.log(Level.INFO, "Failed to stop database", ex);
      }
	}

	protected void startLDAP() throws Exception
   {
		//super.setUp();

		openDSService = new OpenDSService("target/test-classes/opends");
		openDSService.start();
	}

	protected void stopLDAP() throws Exception {
		openDSService.stop();
	}

	public void populateLDIF(String ldifRelativePath) throws Exception {
		File ldif = new File(ldifRelativePath);

		System.out.println("LDIF: " + ldif.getAbsolutePath());

		String[] cmd = new String[] { "-h", LDAP_HOST, "-p", LDAP_PORT, "-D", LDAP_PRINCIPAL, "-w", LDAP_CREDENTIALS, "-a", "-f", ldif.getPath() };

		logger.fine("Populate success: " + (LDAPModify.mainModify(cmd, false, System.out, System.err) == 0));

	}

	public void cleanUpDN(String dn) throws Exception {
		DirContext ldapCtx = getLdapContext();

		try {
			logger.fine("Removing: " + dn);

			removeContext(ldapCtx, dn);
		} catch (Exception e) {
			//
		} finally {
			ldapCtx.close();
		}
	}

	// subsequent remove of javax.naming.Context
	public void removeContext(Context mainCtx, String name) throws Exception {
		Context deleteCtx = (Context) mainCtx.lookup(name);
		NamingEnumeration subDirs = mainCtx.listBindings(name);

		while (subDirs.hasMoreElements()) {
			Binding binding = (Binding) subDirs.nextElement();
			String subName = binding.getName();

			removeContext(deleteCtx, subName);
		}

		mainCtx.unbind(name);
	}

	public LdapContext getLdapContext() throws Exception {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, LDAP_PROVIDER_URL);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, LDAP_PRINCIPAL);
		env.put(Context.SECURITY_CREDENTIALS, LDAP_CREDENTIALS);

		return new InitialLdapContext(env, null);
	}

	// performance stuff

	private Map<Integer, Number> measure;

	private long starttime;
	private long stoptime;

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
		if (isGraph)
			measure.put(i, result);

		if (isFileLog) {
			try {
				FileWriter fw = new FileWriter(new File( test_id + ".log"), true);
				PrintWriter pw = new PrintWriter(fw);
				pw.println(i + "," + result);
				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

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
				"time to create one" + " ... S:" + sum + ", " + "A: " + ((double)sum / measure.size()), // y-axis
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

        protected String getGraphNamePfx(){
            //return this.getClass().getPackage().toString() +"."+ this.getClass().getName();
            return this.getClass().getName();
        }

}
