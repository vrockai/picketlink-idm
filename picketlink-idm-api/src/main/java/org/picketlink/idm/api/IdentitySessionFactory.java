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

package org.picketlink.idm.api;

import java.util.Map;

import org.picketlink.idm.common.exception.IdentityException;

/**
 * Creates IdentitySession objects for a given Realm. An IdentitySessionFactory
 * is typically created from an IdentityConfiguration.
 *
 * @see org.picketlink.idm.api.cfg.IdentityConfiguration#buildIdentitySessionFactory()
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @author Shane Bryzak
 * @version : 0.1 $
 */
public interface IdentitySessionFactory
{
   /**
    * Close IdentitySessionFactory
    */
   void close();

   /**
    * @return if closed
    */
   boolean isClosed();

   /**
    * Creates a new IdentitySession.
    *
    * @param realmName The name of the realm
    * @return The new IdentitySession
    */
   IdentitySession createIdentitySession(String realmName) throws IdentityException;


   /**
    * Creates a new IdentitySession for the given realm.  This method accepts
    * a sessionOptions parameter which may be used to customize the IdentitySession
    * instance returned.
    *
    * @param realmName The name of the realm
    * @param sessionOptions A Map containing session options
    * @return The new IdentitySession
    * @throws IdentityException
    */
  IdentitySession createIdentitySession(String realmName,
        Map<String,Object> sessionOptions) throws IdentityException;

   /**
    * Get current open session associated with a realm. If not present, creates one
    * @return
    */
   IdentitySession getCurrentIdentitySession(String realmName) throws IdentityException;

}
