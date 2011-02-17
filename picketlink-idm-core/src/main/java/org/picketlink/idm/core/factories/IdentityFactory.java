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
package org.picketlink.idm.core.factories;

import java.util.HashSet;
import java.util.Set;

import org.picketlink.idm.api.cfg.IdentityConfiguration;
import org.picketlink.idm.common.exception.IdentityConfigurationException;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.configuration.IdentityConfigurationImpl;
import org.picketlink.idm.spi.model.IdentityObjectCredentialType;
import org.picketlink.idm.spi.model.IdentityObjectRelationshipType;
import org.picketlink.idm.spi.model.IdentityObjectType;
import org.picketlink.idm.spi.store.FeaturesMetaData;
import org.picketlink.idm.spi.store.IdentityObjectSearchCriteriaType;

/**
 * Static Factory to obtain the default factories
 * @author Anil.Saldhana@redhat.com
 * @since Feb 14, 2011
 */
public class IdentityFactory
{
   /**
    * Given a configuration file, return {@link IdentityConfiguration}
    * @param configFileName
    * @return
    * @throws IdentityConfigurationException
    */
   public static IdentityConfiguration createConfiguration( String configFileName ) throws IdentityConfigurationException
   {
      IdentityConfigurationImpl config = new IdentityConfigurationImpl();
      return config.configure( configFileName );
   }
   
   public static FeaturesMetaData createEmptyFeaturesMetaData()
   {
      return new InternalFeaturesMetadata();
   }
   
   private static final class InternalFeaturesMetadata implements FeaturesMetaData
   {   
      public boolean isSearchCriteriaTypeSupported(IdentityObjectType identityObjectType,
            IdentityObjectSearchCriteriaType storeSearchConstraint)
      {
         return false;
      }
      
      public boolean isRoleNameSearchCriteriaTypeSupported(IdentityObjectSearchCriteriaType constraint)
      {
         return false;
      }
      
      public boolean isRelationshipTypeSupported(IdentityObjectType fromType, IdentityObjectType toType,
            IdentityObjectRelationshipType relationshipType) throws IdentityException
      {
         return false;
      }
      
      public boolean isRelationshipPropertiesSupported()
      {
         return false;
      }
      
      public boolean isRelationshipNameAddRemoveSupported()
      {
         return false;
      }
      
      public boolean isNamedRelationshipsSupported()
      {
         return false;
      }
      
      public boolean isIdentityObjectTypeSupported(IdentityObjectType identityObjectType)
      {
         return false;
      }
      
      public boolean isIdentityObjectAddRemoveSupported(IdentityObjectType objectType)
      {
         return false;
      }
      
      public boolean isCredentialSupported(IdentityObjectType identityObjectType,
            IdentityObjectCredentialType credentialType)
      {
         return false;
      }
      
      public Set<String> getSupportedRelationshipTypes()
      {
          return new HashSet<String>();
      }
      
      public Set<String> getSupportedIdentityObjectTypes()
      { 
         return new HashSet<String>();
      }
   }
}