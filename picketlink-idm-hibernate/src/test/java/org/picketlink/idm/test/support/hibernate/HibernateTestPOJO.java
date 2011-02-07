package org.picketlink.idm.test.support.hibernate;

import org.jboss.portal.test.framework.embedded.ConnectionManagerSupport;
import org.jboss.portal.test.framework.embedded.DataSourceSupport;
import org.jboss.portal.test.framework.embedded.HibernateSupport;
import org.jboss.portal.test.framework.embedded.JNDISupport;
import org.jboss.portal.test.framework.embedded.TransactionManagerSupport;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import org.hibernate.SessionFactory;
import org.picketlink.idm.test.support.IdentityTestPOJO;

public class HibernateTestPOJO extends IdentityTestPOJO
{

   protected String dataSourceName = "hsqldb";

   protected String hibernateConfig = "datasources/hibernates.xml";

   protected String datasources = "datasources/datasources.xml";

   protected JNDISupport jndiSupport;

   protected TransactionManagerSupport transactonManagerSupport;

   protected ConnectionManagerSupport connectionManagerSupport;

   protected DataSourceSupport dataSourceSupport;

   protected HibernateSupport hibernateSupport;



   public void start() throws Exception
   {
      overrideFromProperties();

      jndiSupport = new JNDISupport();
      jndiSupport.start();
      transactonManagerSupport = new TransactionManagerSupport();
      transactonManagerSupport.start();
      connectionManagerSupport = new ConnectionManagerSupport();
      connectionManagerSupport.setTransactionManager(transactonManagerSupport.getTransactionManager());
      connectionManagerSupport.start();



      DataSourceSupport.Config dataSourceConfig = DataSourceSupport.Config.obtainConfig(datasources, dataSourceName);

      HibernateSupport.Config hibernateSupportConfig = HibernateSupport.getConfig(dataSourceName, hibernateConfig);

      dataSourceSupport = new DataSourceSupport();
      dataSourceSupport.setTransactionManager(transactonManagerSupport.getTransactionManager());
      dataSourceSupport.setConnectionManagerReference(connectionManagerSupport.getConnectionManagerReference());
      dataSourceSupport.setConfig(dataSourceConfig);
      dataSourceSupport.start();

//      hibernateSupport = new HibernateAnnotationsSupport();
      hibernateSupport = new HibernateSupport();
      hibernateSupport.setConfig(hibernateSupportConfig);
      hibernateSupport.setJNDIName("java:/jbossidentity/HibernateStoreSessionFactory");

      String prefix = "mappings/";

      //Sybase support hack
      if (dataSourceName.startsWith("sybase-"))
      {
         prefix = "sybase-mappings/";
      }

      List<String> mappings = new LinkedList<String>();
      mappings.add(prefix + "HibernateIdentityObject.hbm.xml");
      mappings.add(prefix + "HibernateIdentityObjectCredentialBinaryValue.hbm.xml");
      mappings.add(prefix + "HibernateIdentityObjectAttributeBinaryValue.hbm.xml");
      mappings.add(prefix + "HibernateIdentityObjectAttribute.hbm.xml");
      mappings.add(prefix + "HibernateIdentityObjectCredential.hbm.xml");
      mappings.add(prefix + "HibernateIdentityObjectCredentialType.hbm.xml");
      mappings.add(prefix + "HibernateIdentityObjectRelationship.hbm.xml");
      mappings.add(prefix + "HibernateIdentityObjectRelationshipName.hbm.xml");
      mappings.add(prefix + "HibernateIdentityObjectRelationshipType.hbm.xml");
      mappings.add(prefix + "HibernateIdentityObjectType.hbm.xml");
      mappings.add(prefix + "HibernateRealm.hbm.xml");

      hibernateSupport.setMappings(mappings);

      hibernateSupport.start();


   }

   public void stop() throws Exception
   {
      hibernateSupport.getSessionFactory().getStatistics().logSummary();
      hibernateSupport.stop();
      dataSourceSupport.stop();
      connectionManagerSupport.stop();
      transactonManagerSupport.stop();
      jndiSupport.stop();


   }

   public void overrideFromProperties() throws Exception
   {
      String dsName = System.getProperties().getProperty("dataSourceName");

      if (dsName != null && !dsName.startsWith("$"))
      {
         setDataSourceName(dsName);
      }

   }

   public SessionFactory getSessionFactory()
   {
      return getHibernateSupport().getSessionFactory();
   }

   public void setDataSourceName(String dataSourceName)
   {
      this.dataSourceName = dataSourceName;
   }

   public void setHibernateConfig(String hibernateConfig)
   {
      this.hibernateConfig = hibernateConfig;
   }

   public void setDatasources(String datasources)
   {
      this.datasources = datasources;
   }

   public String getDataSourceName()
   {
      return dataSourceName;
   }

   public String getHibernateConfig()
   {
      return hibernateConfig;
   }

   public String getDatasources()
   {
      return datasources;
   }

   public HibernateSupport getHibernateSupport()
   {
      return hibernateSupport;
   }

   public void begin()
   {
      getHibernateSupport().openSession();
   }

   public void commit()
   {
      Assert.assertTrue(getHibernateSupport().commitTransaction());
   }


}
