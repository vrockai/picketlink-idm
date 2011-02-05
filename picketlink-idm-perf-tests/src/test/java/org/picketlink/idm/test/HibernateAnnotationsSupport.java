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

package org.picketlink.idm.test;

import org.jboss.portal.test.framework.embedded.HibernateSupport;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.AnnotationConfiguration;

import java.util.Iterator;
import java.util.Properties;
import java.util.Map;

/**
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class HibernateAnnotationsSupport extends HibernateSupport
{

   @Override
   protected void createConfiguration()
   {
      AnnotationConfiguration cfg = new AnnotationConfiguration();

      //
      for (Iterator i = mappings.iterator(); i.hasNext();)
      {
         String mapping = (String)i.next();
         log.debug("Adding annotated enitity " + mapping);
         try
         {
            Class mappedClass = Class.forName(mapping);
            cfg.addAnnotatedClass(mappedClass);
         }
         catch (ClassNotFoundException e)
         {
            throw new IllegalArgumentException("Cannot find mapped class: " + mapping, e);
         }

      }

      //
      Properties props = new Properties();
      for (Iterator i = config.getProperties().entrySet().iterator();i.hasNext();)
      {
         Map.Entry entry = (Map.Entry)i.next();
         String key = (String)entry.getKey();
         String value = (String)entry.getValue();
         log.debug("Adding property " + key + " = " + value);
         cfg.setProperty(key, value);
      }
      cfg.addProperties(props);

      // todo : make this configurable somehow
      cfg.setProperty("hibernate.connection.datasource", "java:/DefaultDS");

      //
      if (jndiName != null)
      {
         log.debug("Setting jndi name to " + jndiName);
         cfg.setProperty("hibernate.session_factory_name", jndiName);
      }

      //
      this.settings = cfg.buildSettings();
      this.cfg = cfg;
   }
}
