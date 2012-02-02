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

package org.picketlink.idm.impl.configuration;

import junit.framework.TestCase;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.picketlink.idm.impl.configuration.jaxb2.JAXB2IdentityConfiguration;
import org.picketlink.idm.impl.configuration.jaxb2.SystemPropertiesSubstitutionConverter;
import org.picketlink.idm.spi.configuration.metadata.IdentityConfigurationMetaData;

/**
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class ConfigurationTestCase extends TestCase
{



   public void testSimple() throws Exception
   {

      IdentityConfigurationMetaData config = JAXB2IdentityConfiguration.createConfigurationMetaData(new File("src/test/resources/store-test-config.xml"));

      assertNotNull(config);

      assertEquals("HibernateTestStore", config.getIdentityStores().get(0).getId() );

      
   }

   public void testSystemPropertiesSubstitution() throws Exception
   {
      // Init system properties for substitution
      System.setProperty("identity.store.id", "HibernateStore");
      System.setProperty("relationship.type1", "JBOSS_IDENTITY_MEMBERSHIP");
      System.setProperty("relationship.type2", "JBOSS_IDENTITY_ROLLE");
      
      System.setProperty("option2.value", "Value of option2");
      System.setProperty("option3.value", "Value of option3");
      System.setProperty("option5.value1", "Option5 value1");
      System.setProperty("option5.value2", "Option5 value2");
      System.setProperty("option6.value1", "Option6 value1");
      System.setProperty("option7.value2", "Option7 value2");
      System.setProperty("option8.value2", "Option8 value2");
      System.setProperty("option8.value4", "Option8 value4");

      // Parse config file
      IdentityConfigurationMetaData config = JAXB2IdentityConfiguration.createConfigurationMetaData(new File("src/test/resources/example-system-properties-config.xml"));
      assertNotNull(config);

      // Assert that values from configuration were correctly substituted
      assertEquals("HibernateStore", config.getRepositories().get(0).getDefaultIdentityStoreId());
      assertEquals("HibernateStore", config.getRepositories().get(0).getDefaultAttributeStoreId());
      assertEquals("true", config.getRepositories().get(0).getOption("allowNotDefinedAttributes").get(0));
      assertEquals("HibernateStore", config.getIdentityStores().get(0).getId());
      assertTrue(config.getIdentityStores().get(0).getSupportedRelationshipTypes().contains("JBOSS_IDENTITY_MEMBERSHIP"));
      assertTrue(config.getIdentityStores().get(0).getSupportedRelationshipTypes().contains("JBOSS_IDENTITY_ROLLE"));

      // Assert that all options were correctly substituted
      Map<String, List<String>> options = config.getIdentityStores().get(0).getOptions();
      assertEquals("option1Value", options.get("option1").get(0));
      assertEquals("Value of option2", options.get("option2").get(0));
      assertEquals("Value of option3", options.get("option3").get(0));
      assertEquals("defaultValue", options.get("option4").get(0));
      assertEquals("Option5 value1", options.get("option5").get(0));
      assertEquals("Option6 value1", options.get("option6").get(0));
      assertEquals("Option7 value2", options.get("option7").get(0));
      assertEquals("something1 Option8 value2 something2 defaultValue something3 Option8 value4 something4", options.get("option8").get(0));
      assertEquals("something1 ${} something2", options.get("option9").get(0));
      assertEquals("${option10.value1}", options.get("option10").get(0));
      assertEquals("value2", options.get("option10").get(1));
      assertEquals("defaultValue3", options.get("option10").get(2));
   }
}
