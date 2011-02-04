
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

package org.picketlink.idm.impl.store.ldap;

import java.io.Serializable;

import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;


/**
 * Class that helps to repackage needed content from JNDI SearchResult into serializable object
 *                                                                
 */
public class SerializableSearchResult implements Serializable
{

   private final Attributes attributes;

   private final String nameInNamespace;

   public SerializableSearchResult(SearchResult result)
   {
      nameInNamespace = result.getNameInNamespace();
      attributes = result.getAttributes();
   }

   public Attributes getAttributes()
   {
      return attributes;
   }

   public String getNameInNamespace()
   {
      return nameInNamespace;
   }
}
