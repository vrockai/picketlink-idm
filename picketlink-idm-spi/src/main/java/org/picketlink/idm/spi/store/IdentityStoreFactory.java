/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.picketlink.idm.spi.store;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * PLIDM-23: Factory that returns the Identity Store implementations related to ldap, db etc
 * @author Anil.Saldhana@redhat.com
 * @since Aug 24, 2010
 */
public class IdentityStoreFactory
{   
   public enum IdentityStoreType
   {
      LDAP, DB, HIBERNATE;
   } 
   
   /**
    * A map of FQN of store implementations that are keyed in by the Identity Store Type
    */
   private static Map<IdentityStoreType, String> classMap = new HashMap<IdentityStoreType, String>();
   
   /**
    * A map of concrete store implementations that are keyed in by the Identity Store Type
    */
   private static Map<IdentityStoreType, IdentityStore> instanceMap = new HashMap<IdentityStoreType, IdentityStore>();
   
   static
   {
      classMap.put( IdentityStoreType.LDAP, "org.picketlink.idm.impl.store.ldap.LDAPIdentityStoreImpl" );
      classMap.put( IdentityStoreType.HIBERNATE, "org.picketlink.idm.impl.store.hibernate.HibernateIdentityStoreImpl" ); 
   };
   
   /**
    * Given a {@code IdentityStoreType}, return a concrete implementation of store
    * @param type
    * @param id
    * @return
    */
   @SuppressWarnings({"rawtypes", "unchecked"})
   public static IdentityStore getIdentityStore( IdentityStoreType type, String id )
   {
      IdentityStore store = instanceMap.get(type);
      if( store == null )
      {
         ClassLoader tcl = SecurityActions.getContextClassLoader();
         try
         {
            Class clazz = tcl.loadClass( classMap.get(type) );
            Constructor ctr = clazz.getDeclaredConstructor( new Class[] { String.class } );
            store = (IdentityStore) ctr.newInstance( new Object[] { id } );
            instanceMap.put(type, store);
         }
         catch ( Exception e )
         {
            throw new RuntimeException( "Unable to get Identity Store:", e );
         }
      }
      return store; 
   }
   
   /**
    * Static Method that replaces the fqn of store implementation for a particular identity type
    * @param type
    * @param fqn
    */
   public void setIdentityStoreClass( IdentityStoreType type, String fqn )
   {
      if( fqn == null )
         throw new IllegalArgumentException( "fqn is null" );
      classMap.put(type, fqn); 
      instanceMap.remove( type ); //Remove the old type
   }
}