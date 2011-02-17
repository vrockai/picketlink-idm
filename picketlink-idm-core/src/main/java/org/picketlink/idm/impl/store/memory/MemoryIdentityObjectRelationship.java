/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors. 
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
package org.picketlink.idm.impl.store.memory;

import org.picketlink.idm.spi.model.IdentityObject;
import org.picketlink.idm.spi.model.IdentityObjectRelationship;
import org.picketlink.idm.spi.model.IdentityObjectRelationshipType;

/**
 * A {@link IdentityObjectRelationship} for use in the
 * {@link MemoryIdentityStore}
 * @author Anil.Saldhana@redhat.com
 * @since Feb 14, 2011
 */
public class MemoryIdentityObjectRelationship implements IdentityObjectRelationship
{
   private String name;
   private IdentityObjectRelationshipType type;
   private IdentityObject to;
   private IdentityObject from;

   public MemoryIdentityObjectRelationship( String name, IdentityObjectRelationshipType type,  
         IdentityObject from, IdentityObject to )
   {
      this.name = name;
      this.type = type;
      this.to = to;
      this.from = from;
   }

   public String getName()
   { 
      return name;
   }

   public IdentityObjectRelationshipType getType()
   {
      return type;
   }

   public IdentityObject getFromIdentityObject()
   { 
      return from;
   }

   public IdentityObject getToIdentityObject()
   { 
      return to;
   } 
}