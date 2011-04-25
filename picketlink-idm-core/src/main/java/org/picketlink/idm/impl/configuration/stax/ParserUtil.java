package org.picketlink.idm.impl.configuration.stax;
/**
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.picketlink.idm.impl.api.attribute.IdentityObjectAttributeMetaDataImpl;
import org.picketlink.idm.spi.configuration.metadata.IdentityObjectAttributeMetaData;
import org.staxnav.StaxNavigator;

/**
 * Helper class for parsing common parts of different config files
 *
 * @author Boleslaw Dawidowicz
 */
public class ParserUtil
{

   /**
    * Parse content of <options> element.
    *
    * <options>
    *    <option>
    *       <name></name>
    *       <value></value>
    *       <value></value>
    *    </option>
    *    ...
    *
    * @param nav
    * @return
    */
   static Map<String, List<String>> parseOptions(StaxNavigator<String> nav)
   {
      Map<String, List<String>> options = new HashMap<String, List<String>>();

      for (StaxNavigator<String> fork : nav.fork("option"))
      {
         String name = null;
         List<String> values = new LinkedList<String>();
         String tag = fork.next();


         while(tag != null)
         {
            if (tag.equals("name"))
            {
               name = fork.getContent();
            }
            else if (tag.equals("value"))
            {
               values.add(fork.getContent());
            }

            tag = fork.next();
         }

         if (name != null && name.length() > 0)
         {
            options.put(name, values);
         }
      }

      return options;
   }

   /**
    * Parse content of <attributes> element.
    *   <attributes>
    *    <attribute>
    *     <name>picture</name>
    *     <mapping>user.picture</mapping>
    *     <type>binary</type>
    *     <isRequired>false</isRequired>
    *     <isMultivalued>false</isMultivalued>
    *     <isReadOnly>false</isReadOnly>
    *   </attribute>
    *   ...
    *
    * @param nav
    * @return
    */
   static List<IdentityObjectAttributeMetaData> parseAttributes(StaxNavigator<String> nav)
   {


      List<IdentityObjectAttributeMetaData> attributes = new LinkedList<IdentityObjectAttributeMetaData>();

      for (StaxNavigator<String> fork : nav.fork("attribute"))
      {

         // Use some good defaults
         String name = null;
         String mapping = null;
         String type = "text";
         boolean isRequired = false;
         boolean isMultivalued = false;
         boolean isReadOnly = false;
         boolean isUnique = false;

         String tag = fork.next();

         while(tag != null)
         {

            if (tag.equals("name"))
            {
               name = fork.getContent();
            }
            else if (tag.equals("mapping"))
            {
               mapping = fork.getContent();
            }
            else if (tag.equals("type"))
            {
               type = fork.getContent();
            }
            else if (tag.equals("isRequired"))
            {
               isRequired = Boolean.valueOf(fork.getContent());
            }
            else if (tag.equals("isMultivalued"))
            {
               isMultivalued = Boolean.valueOf(fork.getContent());
            }
            else if (tag.equals("isReadOnly"))
            {
               isReadOnly = Boolean.valueOf(fork.getContent());
            }
            else if (tag.equals("isUnique"))
            {
               isUnique = Boolean.valueOf(fork.getContent());
            }

            tag = fork.next();

         }

         if (name != null && name.length() > 0)
         {
            attributes.add(new
               IdentityObjectAttributeMetaDataImpl(name, mapping, type, isReadOnly, isMultivalued, isRequired, isUnique));
         }
      }

      return attributes;
   }


   public enum Element
   {
      NAME, VALUE;
   }

}
