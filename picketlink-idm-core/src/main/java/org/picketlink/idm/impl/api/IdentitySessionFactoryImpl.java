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

package org.picketlink.idm.impl.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.IdentitySessionFactory;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.session.IdentitySessionImpl;
import org.picketlink.idm.impl.configuration.IdentitySessionConfigurationContext;

/**
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @author Tom Baeyens
 * @version : 0.1 $
 */
public class IdentitySessionFactoryImpl implements IdentitySessionFactory, Serializable
{

   private static final long serialVersionUID = 3806145082462607359L;

   private final Map<String, IdentitySession> realmMap = new HashMap<String, IdentitySession>();

   private final Map<String, IdentitySessionConfigurationContext> sessionContextMap;

   public IdentitySessionFactoryImpl(Map<String, IdentitySessionConfigurationContext> sessionContextMap)
   {
      this.sessionContextMap = sessionContextMap;
   }

   public void close()
   {
      //TODO: close all sessions and put closed state?
      //TODO: should keep map of all bootstrapped stores/repos and cleanup
   }

   public boolean isClosed()
   {
      return false;
   }

   public IdentitySession createIdentitySession(String realmName) throws IdentityException
   {

      if (!sessionContextMap.containsKey(realmName))
      {
         throw new IdentityException("Cannot find configured realm with a given name: " + realmName);
      }

      //IdentitySession session = new IdentitySessionImpl(realmName, repo, mapper);
      IdentitySessionConfigurationContext sessionConfigCtx = sessionContextMap.get(realmName);

      IdentitySession newSession =
         new IdentitySessionImpl(sessionConfigCtx.getRealmName(),
            sessionConfigCtx.getRepository(),
            sessionConfigCtx.getTypeMapper(),
            sessionConfigCtx.getApiCacheProvider(),
            sessionConfigCtx.getIdentityConfigurationContext(),
            sessionConfigCtx.getRealmOptions());

      realmMap.put(realmName, newSession);

      return newSession;
   }

   public IdentitySession getCurrentIdentitySession(String realmName) throws IdentityException
   {
      if (realmMap.containsKey(realmName))
      {
         return realmMap.get(realmName);
      }
      else
      {
         return createIdentitySession(realmName);
      }
   }
}
