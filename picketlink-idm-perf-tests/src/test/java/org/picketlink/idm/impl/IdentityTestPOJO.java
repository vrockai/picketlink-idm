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

package org.picketlink.idm.impl;

import org.picketlink.idm.impl.model.hibernate.HibernateIdentityObject;
import org.picketlink.idm.impl.model.hibernate.HibernateIdentityObjectAttribute;
//import org.jboss.identity.idm.impl.model.hibernate.HibernateIdentityObjectBinaryAttribute;
//import org.jboss.identity.idm.impl.model.hibernate.HibernateIdentityObjectBinaryAttributeValue;
import org.picketlink.idm.impl.model.hibernate.HibernateIdentityObjectCredential;
import org.picketlink.idm.impl.model.hibernate.HibernateIdentityObjectCredentialType;
import org.picketlink.idm.impl.model.hibernate.HibernateIdentityObjectRelationship;
import org.picketlink.idm.impl.model.hibernate.HibernateIdentityObjectRelationshipName;
import org.picketlink.idm.impl.model.hibernate.HibernateIdentityObjectRelationshipType;
//import org.jboss.identity.idm.impl.model.hibernate.HibernateIdentityObjectTextAttribute;
import org.picketlink.idm.impl.model.hibernate.HibernateIdentityObjectType;
import org.picketlink.idm.impl.model.hibernate.HibernateRealm;
import org.picketlink.idm.test.HibernateAnnotationsSupport;
import org.jboss.portal.test.framework.embedded.ConnectionManagerSupport;
import org.jboss.portal.test.framework.embedded.DataSourceSupport;
import org.jboss.portal.test.framework.embedded.HibernateSupport;
import org.jboss.portal.test.framework.embedded.JNDISupport;
import org.jboss.portal.test.framework.embedded.TransactionManagerSupport;
import org.jboss.unit.api.pojo.annotations.Parameter;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import org.hibernate.SessionFactory;
import org.picketlink.idm.performance.TestBase;


/**
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class IdentityTestPOJO extends TestBase
{

   protected String identityConfig = "test-identity-config.xml";

   private String realmName;

   private String dataSourceName = "hsqldb";

   private String hibernateConfig = "datasources/hibernates.xml";

   private String datasources = "datasources/datasources.xml";

   private JNDISupport jndiSupport;

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

      hibernateSupport = new HibernateAnnotationsSupport();
      hibernateSupport.setConfig(hibernateSupportConfig);
      hibernateSupport.setJNDIName("java:/jbossidentity/HibernateStoreSessionFactory");

      List<String> annotatedClasses = new LinkedList<String>();
      annotatedClasses.add(HibernateIdentityObject.class.getName());
      annotatedClasses.add(HibernateIdentityObjectAttribute.class.getName());
//      annotatedClasses.add(HibernateIdentityObjectBinaryAttribute.class.getName());
//      annotatedClasses.add(HibernateIdentityObjectBinaryAttributeValue.class.getName());
//      annotatedClasses.add(HibernateIdentityObjectTextAttribute.class.getName());
      annotatedClasses.add(HibernateIdentityObjectCredential.class.getName());
      annotatedClasses.add(HibernateIdentityObjectCredentialType.class.getName());
      annotatedClasses.add(HibernateIdentityObjectRelationship.class.getName());
      annotatedClasses.add(HibernateIdentityObjectRelationshipName.class.getName());
      annotatedClasses.add(HibernateIdentityObjectRelationshipType.class.getName());
      annotatedClasses.add(HibernateIdentityObjectType.class.getName());
      annotatedClasses.add(HibernateRealm.class.getName());

      hibernateSupport.setMappings(annotatedClasses);

      hibernateSupport.start();

      
   }

   public void stop() throws Exception
   {
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




   @Parameter(name="dataSourceName")
   public void setDataSourceName(String dataSourceName)
   {
      this.dataSourceName = dataSourceName;
   }

   @Parameter(name="hibernateConfig")
   public void setHibernateConfig(String hibernateConfig)
   {
      this.hibernateConfig = hibernateConfig;
   }

   @Parameter(name="datasources")
   public void setDatasources(String datasources)
   {
      this.datasources = datasources;
   }

   @Parameter(name="identityConfig")
   public void setIdentityConfig(String identityConfig)
   {
      this.identityConfig = identityConfig;
   }

   @Parameter(name="realmName")
   public void setRealmName(String realmName)
   {
      this.realmName = realmName;
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

   public String getIdentityConfig()
   {
      return identityConfig;
   }

   public String getRealmName()
   {
      return realmName;
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
       getHibernateSupport().commitTransaction();
      //assertTrue(getHibernateSupport().commitTransaction());
   }




}
