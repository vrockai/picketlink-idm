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

package org.picketlink.idm.spi.store;

import org.picketlink.idm.common.exception.IdentityException;

import java.io.Serializable;
import java.util.Map;

/**
 * Factory interface for creating IdentityStoreSession instances.
 *
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @author Shane Bryzak
 * @version : 0.1 $
 */
public interface IdentityStoreSessionFactory extends Serializable
{


   /**
    * @return
    * Creates a new IdentityStoreSession.
    *
    * @return The new IdentityStoreSession
    * @throws IdentityException
    */
   IdentityStoreSession createIdentityStoreSession() throws IdentityException;

   /**
    * Creates an IdentityStoreSession.  This factory method allows additional
    * session options to be passed in which may be used by the factory method
    * implementation to customise the IdentityStoreSession returned.
    *
    * @param sessionOptions A Map containing the session option values
    * @return The new IdentityStoreSession
    * @throws IdentityException
    */
   IdentityStoreSession createIdentityStoreSession(Map<String,Object> sessionOptions) throws IdentityException;

}
