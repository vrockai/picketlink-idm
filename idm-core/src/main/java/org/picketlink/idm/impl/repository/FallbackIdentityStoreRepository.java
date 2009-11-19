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

package org.jboss.identity.idm.impl.repository;

import org.jboss.identity.idm.spi.store.IdentityStore;
import org.jboss.identity.idm.spi.store.AttributeStore;
import org.jboss.identity.idm.spi.store.FeaturesMetaData;
import org.jboss.identity.idm.spi.store.IdentityStoreInvocationContext;
import org.jboss.identity.idm.spi.store.IdentityStoreSession;
import org.jboss.identity.idm.spi.store.IdentityObjectSearchCriteriaType;
import org.jboss.identity.idm.spi.model.IdentityObject;
import org.jboss.identity.idm.spi.model.IdentityObjectType;
import org.jboss.identity.idm.spi.model.IdentityObjectRelationshipType;
import org.jboss.identity.idm.spi.model.IdentityObjectRelationship;
import org.jboss.identity.idm.spi.exception.OperationNotSupportedException;
import org.jboss.identity.idm.spi.configuration.metadata.IdentityRepositoryConfigurationMetaData;
import org.jboss.identity.idm.spi.configuration.metadata.IdentityObjectAttributeMetaData;
import org.jboss.identity.idm.spi.configuration.IdentityRepositoryConfigurationContext;
import org.jboss.identity.idm.spi.configuration.IdentityStoreConfigurationContext;
import org.jboss.identity.idm.spi.model.IdentityObjectCredential;
import org.jboss.identity.idm.spi.model.IdentityObjectCredentialType;
import org.jboss.identity.idm.spi.model.IdentityObjectAttribute;
import org.jboss.identity.idm.spi.search.IdentityObjectSearchCriteria;
import org.jboss.identity.idm.common.exception.IdentityException;
import org.jboss.identity.idm.impl.store.SimpleIdentityStoreInvocationContext;
import org.jboss.identity.idm.impl.api.session.managers.RoleManagerImpl;

import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * <p>In FallbackIdentityStoreRepository one IdentityStore plays the role of default store. Any operation that cannot be
 * handled with other IdentityObjectType/IdentityStore mappings will fallback to such IdentityStore. The most common example
 * is RDBMS + LDAP configuration. LDAP has limmited schema for possible profile attributes so for LDAP entries part of
 * profile can be stored in RDBMS by syncing entries into default store.</p>
 * <p>For any relationship that is not supported in other stores, or between entries persisted in two different stores,
 * proper IdentityObjects will be synced to default store and if possible, such relationship will be created. </p>
 *
 *
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class FallbackIdentityStoreRepository extends AbstractIdentityStoreRepository
{
   //TODO: - filter out criteria based on features MD before passing
   //TODO: - configuration option to store not mapped attributes in default store
   //TODO: - configuration option to fallback named relationships to default store when not supported in mapped one

   private final String id;

   //TODO: rewrite this to other config object?
   @SuppressWarnings("unused")
   private IdentityRepositoryConfigurationMetaData configurationMD;

   public static final String ALLOW_NOT_DEFINED_ATTRIBUTES = "allowNotDefinedAttributes";

   private FeaturesMetaData featuresMetaData;
   
   private boolean allowNotDefinedAttributes = false;

   private final Set<IdentityStore> configuredIdentityStores = new HashSet<IdentityStore>();

   public FallbackIdentityStoreRepository(String id)
   {
      this.id = id;
   }

   @Override
   public void bootstrap(IdentityRepositoryConfigurationContext configurationContext,
                         Map<String, IdentityStore> bootstrappedIdentityStores,
                         Map<String, AttributeStore> bootstrappedAttributeStores) throws IdentityException
   {
      super.bootstrap(configurationContext, bootstrappedIdentityStores, bootstrappedAttributeStores);

      // Helper collection to keep all identity stores in use

      if (getIdentityStoreMappings().size() > 0)
      {
         configuredIdentityStores.addAll(getIdentityStoreMappings().values());
      }
      


      this.configurationMD = configurationContext.getRepositoryConfigurationMetaData();

      String isId = configurationMD.getDefaultIdentityStoreId();

      if (isId != null && bootstrappedIdentityStores.keySet().contains(isId))
      {
         if (!getIdentityStoreMappings().keySet().contains(defaultIdentityStore.getId()))
         {
            configuredIdentityStores.add(defaultIdentityStore);
         }

      }

      String allowNotDefineAttributes = configurationMD.getOptionSingleValue(ALLOW_NOT_DEFINED_ATTRIBUTES);

      if (allowNotDefineAttributes != null && allowNotDefineAttributes.equalsIgnoreCase("true"))
      {
         this.allowNotDefinedAttributes = true;
      }

      // A wrapper around all stores features meta data
      featuresMetaData = new FeaturesMetaData()
      {

         public boolean isNamedRelationshipsSupported()
         {
            // If there is any IdentityStore that supports named relationships...
            for (IdentityStore identityStore : getIdentityStoreMappings().values())
            {
               if (identityStore.getSupportedFeatures().isNamedRelationshipsSupported())
               {
                  return true;
               }
            }
            return defaultIdentityStore.getSupportedFeatures().isNamedRelationshipsSupported();
         }

         public boolean isRelationshipPropertiesSupported()
         {
             // If there is any IdentityStore that supports relationship properties...
            for (IdentityStore identityStore : getIdentityStoreMappings().values())
            {
               if (identityStore.getSupportedFeatures().isRelationshipPropertiesSupported())
               {
                  return true;
               }
            }
            return defaultIdentityStore.getSupportedFeatures().isRelationshipPropertiesSupported();
         }

         public boolean isSearchCriteriaTypeSupported(IdentityObjectType identityObjectType, IdentityObjectSearchCriteriaType storeSearchConstraint)
         {
            return resolveIdentityStore(identityObjectType).getSupportedFeatures().isSearchCriteriaTypeSupported(identityObjectType, storeSearchConstraint);
         }

         public Set<String> getSupportedIdentityObjectTypes()
         {
            Set<String> supportedIOTs = new HashSet<String>();

            for (IdentityStore identityStore : getIdentityStoreMappings().values())
            {
               supportedIOTs.addAll(identityStore.getSupportedFeatures().getSupportedIdentityObjectTypes());
            }
            supportedIOTs.addAll(defaultIdentityStore.getSupportedFeatures().getSupportedRelationshipTypes());

            return supportedIOTs;
         }

         public boolean isIdentityObjectTypeSupported(IdentityObjectType identityObjectType)
         {
            return resolveIdentityStore(identityObjectType).getSupportedFeatures().isIdentityObjectTypeSupported(identityObjectType);
         }

         public boolean isRelationshipTypeSupported(IdentityObjectType fromType, IdentityObjectType toType, IdentityObjectRelationshipType relationshipType) throws IdentityException
         {
            IdentityStore fromStore = resolveIdentityStore(fromType);

            IdentityStore toStore = resolveIdentityStore(toType);

            if (fromStore == toStore)
            {
               return fromStore.getSupportedFeatures().isRelationshipTypeSupported(fromType, toType, relationshipType);
            }
            else
            {
               return defaultIdentityStore.getSupportedFeatures().isRelationshipTypeSupported(fromType, toType, relationshipType);
            }

         }

         public Set<String> getSupportedRelationshipTypes()
         {
            Set<String> supportedRelTypes = new HashSet<String>();

            for (IdentityStore identityStore : getIdentityStoreMappings().values())
            {
               supportedRelTypes.addAll(identityStore.getSupportedFeatures().getSupportedRelationshipTypes());
            }
            supportedRelTypes.addAll(defaultIdentityStore.getSupportedFeatures().getSupportedRelationshipTypes());

            return supportedRelTypes;
         }

         public boolean isCredentialSupported(IdentityObjectType identityObjectType, IdentityObjectCredentialType credentialType)
         {
            return resolveIdentityStore(identityObjectType).getSupportedFeatures().isCredentialSupported(identityObjectType, credentialType);
         }

         public boolean isIdentityObjectAddRemoveSupported(IdentityObjectType objectType)
         {
            return resolveIdentityStore(objectType).getSupportedFeatures().isIdentityObjectAddRemoveSupported(objectType);
         }

         public boolean isRelationshipNameAddRemoveSupported()
         {
            // If there is any IdentityStore that supports named relationships...
            for (IdentityStore identityStore : getIdentityStoreMappings().values())
            {
               if (identityStore.getSupportedFeatures().isRelationshipNameAddRemoveSupported())
               {
                  return true;
               }
            }
            return defaultIdentityStore.getSupportedFeatures().isRelationshipNameAddRemoveSupported();

         }

         public boolean isRoleNameSearchCriteriaTypeSupported(IdentityObjectSearchCriteriaType constraint)
         {
            // If there is any IdentityStore that supports named relationships...
            for (IdentityStore identityStore : getIdentityStoreMappings().values())
            {
               if (identityStore.getSupportedFeatures().isNamedRelationshipsSupported() &&
                  identityStore.getSupportedFeatures().isRoleNameSearchCriteriaTypeSupported(constraint))
               {
                  return true;
               }
            }
            return defaultIdentityStore.getSupportedFeatures().isNamedRelationshipsSupported() &&
               defaultIdentityStore.getSupportedFeatures().isRoleNameSearchCriteriaTypeSupported(constraint);
         }

      };

   }

   public void bootstrap(IdentityStoreConfigurationContext configurationContext) throws IdentityException
   {
      // Nothing
   }

   public IdentityStoreSession createIdentityStoreSession() throws IdentityException
   {
      Map<String, IdentityStoreSession> sessions = new HashMap<String, IdentityStoreSession>();

      for (IdentityStore identityStore : identityStoreMappings.values())
      {
         sessions.put(identityStore.getId(), identityStore.createIdentityStoreSession());
      }

      for (AttributeStore attributeStore : attributeStoreMappings.values())
      {
         if (!sessions.containsKey(attributeStore.getId()))
         {
            sessions.put(attributeStore.getId(), attributeStore.createIdentityStoreSession());
         }
      }

      if (!sessions.containsKey(defaultAttributeStore.getId()))
      {
         sessions.put(defaultAttributeStore.getId(), defaultAttributeStore.createIdentityStoreSession());
      }

      if (!sessions.containsKey(defaultIdentityStore.getId()))
      {
         sessions.put(defaultIdentityStore.getId(), defaultIdentityStore.createIdentityStoreSession());
      }

      return new RepositoryIdentityStoreSessionImpl(sessions);
   }

   public String getId()
   {
      return id;
   }

   public FeaturesMetaData getSupportedFeatures()
   {
      return featuresMetaData;
   }

   IdentityStore resolveIdentityStore(IdentityObject io) throws IdentityException
   {
      return resolveIdentityStore(io.getIdentityType());
   }

   IdentityStore resolveIdentityStore(IdentityObjectType iot)
   {

      IdentityStore ids = null;
      try
      {
         ids = getIdentityStore(iot);
      }
      catch (IdentityException e)
      {
         if (isAllowNotDefinedAttributes())
         {
            return defaultIdentityStore;
         }

         throw new IllegalStateException("Used IdentityObjectType not mapped. Consider using " + ALLOW_NOT_DEFINED_IDENTITY_OBJECT_TYPES_OPTION +
            " repository option switch: " + iot );
      }

      if (ids == null)
      {
         ids = defaultIdentityStore;
      }
      return ids;
   }

   AttributeStore resolveAttributeStore(IdentityObjectType iot)
   {
      AttributeStore ads = null;
      try
      {
         ads = getAttributeStore(iot);
      }
      catch (IdentityException e)
      {
         if (isAllowNotDefinedAttributes())
         {
            return defaultAttributeStore;
         }

         throw new IllegalStateException("Used IdentityObjectType not mapped. Consider using " + ALLOW_NOT_DEFINED_IDENTITY_OBJECT_TYPES_OPTION +
         " repository option switch: " + iot );
      }

      if (ads == null)
      {
         ads = defaultAttributeStore;
      }
      return ads;
   }

   IdentityStoreInvocationContext resolveInvocationContext(IdentityStore targetStore, IdentityStoreInvocationContext invocationCtx)
   {
      return resolveInvocationContext(targetStore.getId(), invocationCtx);

   }

   IdentityStoreInvocationContext resolveInvocationContext(AttributeStore targetStore, IdentityStoreInvocationContext invocationCtx)
   {
      return resolveInvocationContext(targetStore.getId(), invocationCtx);

   }

   IdentityStoreInvocationContext resolveInvocationContext(String id, IdentityStoreInvocationContext invocationCtx)
   {
      RepositoryIdentityStoreSessionImpl repoSession = (RepositoryIdentityStoreSessionImpl)invocationCtx.getIdentityStoreSession();
      IdentityStoreSession targetSession = repoSession.getIdentityStoreSession(id);

      return new SimpleIdentityStoreInvocationContext(targetSession, invocationCtx.getRealmId(), String.valueOf(this.hashCode()));

   }

   public IdentityObject createIdentityObject(IdentityStoreInvocationContext invocationCtx, String name, IdentityObjectType identityObjectType) throws IdentityException
   {
      IdentityStore targetStore = resolveIdentityStore(identityObjectType);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(targetStore, invocationCtx);
      return targetStore.createIdentityObject(targetCtx, name, identityObjectType);
   }

   public IdentityObject createIdentityObject(IdentityStoreInvocationContext invocationCtx, String name, IdentityObjectType identityObjectType, Map<String, String[]> attributes) throws IdentityException
   {
      IdentityStore targetStore = resolveIdentityStore(identityObjectType);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(targetStore, invocationCtx);
      return targetStore.createIdentityObject(targetCtx, name, identityObjectType, attributes);
   }

   public void removeIdentityObject(IdentityStoreInvocationContext invocationCtx, IdentityObject identity) throws IdentityException
   {
      IdentityStore targetStore = resolveIdentityStore(identity);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(targetStore, invocationCtx);

      targetStore.removeIdentityObject(targetCtx, identity);
   }

   public int getIdentityObjectsCount(IdentityStoreInvocationContext invocationCtx, IdentityObjectType identityType) throws IdentityException
   {
      IdentityStore targetStore = resolveIdentityStore(identityType);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(targetStore, invocationCtx);

      return targetStore.getIdentityObjectsCount(targetCtx, identityType);
   }

   public IdentityObject findIdentityObject(IdentityStoreInvocationContext invocationContext, String name, IdentityObjectType identityObjectType) throws IdentityException
   {
      IdentityStore targetStore = resolveIdentityStore(identityObjectType);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(targetStore, invocationContext);

      return targetStore.findIdentityObject(targetCtx, name, identityObjectType);
   }

   public IdentityObject findIdentityObject(IdentityStoreInvocationContext invocationContext, String id) throws IdentityException
   {
      //TODO: information about the store mapping should be encoded in id as now its like random guess and this kills performance ...

      for (IdentityStore identityStore : getIdentityStoreMappings().values())
      {
         IdentityStoreInvocationContext targetCtx = resolveInvocationContext(identityStore, invocationContext);

         IdentityObject io = identityStore.findIdentityObject(targetCtx, id);
         if (io != null)
         {
            return io;
         }
      }

      return defaultIdentityStore.findIdentityObject(invocationContext, id);
   }

   public Collection<IdentityObject> findIdentityObject(IdentityStoreInvocationContext invocationCtx, IdentityObjectType identityType, IdentityObjectSearchCriteria criteria) throws IdentityException
   {
      IdentityStore targetStore = resolveIdentityStore(identityType);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(targetStore, invocationCtx);

      return targetStore.findIdentityObject(targetCtx, identityType, criteria);
   }

   public Collection<IdentityObject> findIdentityObject(IdentityStoreInvocationContext invocationCxt,
                                                        IdentityObject identity,
                                                        IdentityObjectRelationshipType relationshipType,
                                                        boolean parent,
                                                       IdentityObjectSearchCriteria criteria) throws IdentityException
   {
      // Check in the mapped store and merge with default

      IdentityStore mappedStore = resolveIdentityStore(identity);

      IdentityStoreInvocationContext mappedCtx = resolveInvocationContext(mappedStore, invocationCxt);

      IdentityStoreInvocationContext defaultCtx = resolveInvocationContext(defaultIdentityStore, invocationCxt);


      if (mappedStore == defaultIdentityStore)
      {
         return defaultIdentityStore.findIdentityObject(defaultCtx, identity, relationshipType, parent, criteria);
      }

      Collection<IdentityObject> results = new LinkedList<IdentityObject>();

      if (relationshipType == null || !RoleManagerImpl.ROLE.getName().equals(relationshipType.getName()) ||
          mappedStore.getSupportedFeatures().isNamedRelationshipsSupported())
      {
         results = mappedStore.findIdentityObject(mappedCtx, identity, relationshipType, parent, criteria);
      }

      IdentityObject defaultStoreIdentityObject = null;

      try
      {
         defaultStoreIdentityObject = defaultIdentityStore.findIdentityObject(defaultCtx, identity.getName(), identity.getIdentityType());
      }
      catch (IdentityException e)
      {
         return results;
      }

      if (defaultStoreIdentityObject != null)
      {
         Collection<IdentityObject> objects = defaultIdentityStore.findIdentityObject(defaultCtx, identity, relationshipType, parent, criteria);

         // If default store contain related relationships merge and sort/page once more
         if (objects != null && objects.size() != 0)
         {

            results.addAll(objects);

            //TODO: hardcoded - expects List
            if (criteria != null && criteria.isPaged() && results instanceof List)
            {
               results = cutPageFromResults((List<IdentityObject>)results, criteria);
            }

            //TODO: hardcoded - expects List
            if (criteria != null && criteria.isSorted() && results instanceof List)
            {
               sortByName((List<IdentityObject>)results, criteria.isAscending());
            }
         }
      }
      return results;

   }

   public IdentityObjectRelationship createRelationship(IdentityStoreInvocationContext invocationCxt, IdentityObject fromIdentity, IdentityObject toIdentity, IdentityObjectRelationshipType relationshipType, String relationshipName, boolean createNames) throws IdentityException
   {
      IdentityStore fromStore = resolveIdentityStore(fromIdentity);

      IdentityStore toStore = resolveIdentityStore(toIdentity);

      IdentityStoreInvocationContext toTargetCtx = resolveInvocationContext(toStore, invocationCxt);

      IdentityStoreInvocationContext defaultTargetCtx = resolveInvocationContext(defaultIdentityStore, invocationCxt);

      if (fromStore == toStore)
      {
         // If relationship is named and target store doesn't support named relationships it need to be put in default store anyway
         if (relationshipName == null ||
            (relationshipName != null && fromStore.getSupportedFeatures().isNamedRelationshipsSupported()))
         {
            return fromStore.createRelationship(toTargetCtx, fromIdentity, toIdentity, relationshipType, relationshipName, createNames);
         }
      }

      if (!hasIdentityObject(defaultTargetCtx, defaultIdentityStore, fromIdentity))
      {
         defaultIdentityStore.createIdentityObject(defaultTargetCtx, fromIdentity.getName(),  fromIdentity.getIdentityType());
      }

      if (!hasIdentityObject(defaultTargetCtx, defaultIdentityStore, toIdentity))
      {
         defaultIdentityStore.createIdentityObject(defaultTargetCtx, toIdentity.getName(),  toIdentity.getIdentityType());
      }

      return defaultIdentityStore.createRelationship(defaultTargetCtx, fromIdentity, toIdentity, relationshipType, relationshipName, createNames);
   }

   public void removeRelationship(IdentityStoreInvocationContext invocationCxt, IdentityObject fromIdentity, IdentityObject toIdentity, IdentityObjectRelationshipType relationshipType, String relationshipName) throws IdentityException
   {
      IdentityStore fromStore = resolveIdentityStore(fromIdentity);

      IdentityStore toStore = resolveIdentityStore(toIdentity);

      IdentityStoreInvocationContext toTargetCtx = resolveInvocationContext(toStore, invocationCxt);

      IdentityStoreInvocationContext defaultTargetCtx = resolveInvocationContext(defaultIdentityStore, invocationCxt);

      if (fromStore == toStore)
      {
         if (relationshipName == null ||
            (relationshipName != null && fromStore.getSupportedFeatures().isNamedRelationshipsSupported()))
         {
            fromStore.removeRelationship(toTargetCtx, fromIdentity, toIdentity, relationshipType, relationshipName);
            return;
         }
      }

      if (!hasIdentityObject(defaultTargetCtx, defaultIdentityStore, fromIdentity))
      {
         defaultIdentityStore.createIdentityObject(defaultTargetCtx, fromIdentity.getName(),  fromIdentity.getIdentityType());
      }

      if (!hasIdentityObject(defaultTargetCtx, defaultIdentityStore, toIdentity))
      {
         defaultIdentityStore.createIdentityObject(defaultTargetCtx, toIdentity.getName(),  toIdentity.getIdentityType());
      }

      defaultIdentityStore.removeRelationship(defaultTargetCtx, fromIdentity, toIdentity, relationshipType, relationshipName);
   }

   public void removeRelationships(IdentityStoreInvocationContext invocationCtx, IdentityObject identity1, IdentityObject identity2, boolean named) throws IdentityException
   {
      IdentityStore fromStore = resolveIdentityStore(identity1);

      IdentityStore toStore = resolveIdentityStore(identity2);

      IdentityStoreInvocationContext toTargetCtx = resolveInvocationContext(toStore, invocationCtx);

      IdentityStoreInvocationContext defaultTargetCtx = resolveInvocationContext(defaultIdentityStore, invocationCtx);


      if (fromStore == toStore)
      {
         fromStore.removeRelationships(toTargetCtx, identity1, identity2, named);
         return;
      }

      if (!hasIdentityObject(defaultTargetCtx, defaultIdentityStore, identity1))
      {
         defaultIdentityStore.createIdentityObject(defaultTargetCtx, identity1.getName(),  identity1.getIdentityType());
      }

      if (!hasIdentityObject(defaultTargetCtx, defaultIdentityStore, identity2))
      {
         defaultIdentityStore.createIdentityObject(defaultTargetCtx, identity2.getName(),  identity2.getIdentityType());
      }

      defaultIdentityStore.removeRelationships(defaultTargetCtx, identity1, identity2, named);
   }

   public Set<IdentityObjectRelationship> resolveRelationships(IdentityStoreInvocationContext invocationCxt,
                                                               IdentityObject fromIdentity,
                                                               IdentityObject toIdentity,
                                                               IdentityObjectRelationshipType relationshipType) throws IdentityException
   {

      IdentityStore fromStore = resolveIdentityStore(fromIdentity);

      IdentityStore toStore = resolveIdentityStore(toIdentity);

      IdentityStoreInvocationContext toTargetCtx = resolveInvocationContext(toStore, invocationCxt);

      IdentityStoreInvocationContext defaultTargetCtx = resolveInvocationContext(defaultIdentityStore, invocationCxt);

      if (fromStore == toStore &&
         (!RoleManagerImpl.ROLE.getName().equals(relationshipType.getName()) ||
          fromStore.getSupportedFeatures().isNamedRelationshipsSupported()))
      {
         return fromStore.resolveRelationships(toTargetCtx, fromIdentity, toIdentity, relationshipType);

      }

      if (!hasIdentityObject(defaultTargetCtx, defaultIdentityStore, fromIdentity))
      {
         defaultIdentityStore.createIdentityObject(defaultTargetCtx, fromIdentity.getName(),  fromIdentity.getIdentityType());
      }

      if (!hasIdentityObject(defaultTargetCtx, defaultIdentityStore, toIdentity))
      {
         defaultIdentityStore.createIdentityObject(defaultTargetCtx, toIdentity.getName(),  toIdentity.getIdentityType());
      }

      return defaultIdentityStore.resolveRelationships(defaultTargetCtx, fromIdentity, toIdentity, relationshipType);
   }

   public Set<IdentityObjectRelationship> resolveRelationships(IdentityStoreInvocationContext ctx, IdentityObject identity, IdentityObjectRelationshipType relationshipType, boolean parent, boolean named, String name) throws IdentityException
   {  
      Set<IdentityObjectRelationship> relationships = new HashSet<IdentityObjectRelationship>();

      // For any IdentityStore that supports named relationships...
      for (IdentityStore identityStore : configuredIdentityStores)
      {
         if (!identityStore.getSupportedFeatures().getSupportedRelationshipTypes().contains(relationshipType.getName()))
         {
            continue;
         }

         if (!named || (named && identityStore.getSupportedFeatures().isNamedRelationshipsSupported()))
         {
            IdentityStoreInvocationContext storeCtx = resolveInvocationContext(identityStore, ctx);
            relationships.addAll(identityStore.resolveRelationships(storeCtx, identity, relationshipType, parent, named, name));
         }
      }

      return relationships;
   }

   public String createRelationshipName(IdentityStoreInvocationContext ctx, String name) throws IdentityException, OperationNotSupportedException
   {
      // For any IdentityStore that supports named relationships...
      for (IdentityStore identityStore : configuredIdentityStores)
      {
         if (identityStore.getSupportedFeatures().isNamedRelationshipsSupported())
         {
            IdentityStoreInvocationContext storeCtx = resolveInvocationContext(identityStore, ctx);
            identityStore.createRelationshipName(storeCtx, name);

         }
      }

      return name;
   }

   public String removeRelationshipName(IdentityStoreInvocationContext ctx, String name) throws IdentityException, OperationNotSupportedException
   {

      // For any IdentityStore that supports named relationships...
      for (IdentityStore identityStore : configuredIdentityStores)
      {
         if (identityStore.getSupportedFeatures().isNamedRelationshipsSupported())
         {
            IdentityStoreInvocationContext storeCtx = resolveInvocationContext(identityStore, ctx);
            identityStore.removeRelationshipName(storeCtx, name);

         }
      }

      return name;
   }

   public Set<String> getRelationshipNames(IdentityStoreInvocationContext ctx, IdentityObjectSearchCriteria criteria) throws IdentityException, OperationNotSupportedException
   {
      Set<String> results = new HashSet<String>();

      // For any IdentityStore that supports named relationships...
      for (IdentityStore identityStore : configuredIdentityStores)
      {
         if (identityStore.getSupportedFeatures().isNamedRelationshipsSupported())
         {
            IdentityStoreInvocationContext storeCtx = resolveInvocationContext(identityStore, ctx);
            results.addAll(identityStore.getRelationshipNames(storeCtx, criteria));

         }
      }

      return results;
   }

   public Set<String> getRelationshipNames(IdentityStoreInvocationContext ctx, IdentityObject identity, IdentityObjectSearchCriteria criteria) throws IdentityException, OperationNotSupportedException
   {

      IdentityStore toStore = resolveIdentityStore(identity);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(toStore, ctx);

      if (toStore.getSupportedFeatures().isNamedRelationshipsSupported())
      {
         return toStore.getRelationshipNames(targetCtx, identity, criteria);
      }
      IdentityStoreInvocationContext defaultCtx = resolveInvocationContext(defaultIdentityStore, ctx);

      return defaultIdentityStore.getRelationshipNames(defaultCtx, identity, criteria);
   }

   public Map<String, String> getRelationshipNameProperties(IdentityStoreInvocationContext ctx, String name) throws IdentityException, OperationNotSupportedException
   {
      Map<String, String> results = new HashMap<String, String>();

      // For any IdentityStore that supports named relationships...
      for (IdentityStore identityStore : configuredIdentityStores)
      {
         if (identityStore.getSupportedFeatures().isNamedRelationshipsSupported())
         {
            IdentityStoreInvocationContext storeCtx = resolveInvocationContext(identityStore, ctx);

            Map<String, String> props = identityStore.getRelationshipNameProperties(storeCtx, name);
            if (props != null && props.keySet().size() > 0)
            {
               results.putAll(props);
            }

         }
      }

      return results;
   }

   public void setRelationshipNameProperties(IdentityStoreInvocationContext ctx, String name, Map<String, String> properties) throws IdentityException, OperationNotSupportedException
   {
      // For any IdentityStore that supports named relationships...
      for (IdentityStore identityStore : configuredIdentityStores)
      {
         if (identityStore.getSupportedFeatures().isNamedRelationshipsSupported())
         {
            IdentityStoreInvocationContext storeCtx = resolveInvocationContext(identityStore, ctx);

            identityStore.setRelationshipNameProperties(storeCtx, name, properties);

         }
      }
   }

   public void removeRelationshipNameProperties(IdentityStoreInvocationContext ctx, String name, Set<String> properties) throws IdentityException, OperationNotSupportedException
   {
      // For any IdentityStore that supports named relationships...
      for (IdentityStore identityStore : configuredIdentityStores)
      {
         if (identityStore.getSupportedFeatures().isNamedRelationshipsSupported())
         {
            IdentityStoreInvocationContext storeCtx = resolveInvocationContext(identityStore, ctx);

            identityStore.removeRelationshipNameProperties(storeCtx, name, properties);

         }
      }
   }

   public Map<String, String> getRelationshipProperties(IdentityStoreInvocationContext ctx, IdentityObjectRelationship relationship) throws IdentityException, OperationNotSupportedException
   {

      IdentityStore fromStore = resolveIdentityStore(relationship.getFromIdentityObject());
      IdentityStore toStore = resolveIdentityStore(relationship.getToIdentityObject());

      if (fromStore == toStore && toStore.getSupportedFeatures().isNamedRelationshipsSupported())
      {
         return fromStore.getRelationshipProperties(resolveInvocationContext(fromStore, ctx), relationship);
      }

      return defaultIdentityStore.getRelationshipProperties(resolveInvocationContext(defaultIdentityStore, ctx), relationship);
   }

   public void setRelationshipProperties(IdentityStoreInvocationContext ctx, IdentityObjectRelationship relationship, Map<String, String> properties) throws IdentityException, OperationNotSupportedException
   {
      IdentityStore fromStore = resolveIdentityStore(relationship.getFromIdentityObject());
      IdentityStore toStore = resolveIdentityStore(relationship.getToIdentityObject());

      if (fromStore == toStore && toStore.getSupportedFeatures().isNamedRelationshipsSupported())
      {
         fromStore.setRelationshipProperties(resolveInvocationContext(fromStore, ctx), relationship, properties);
         return;
      }

      defaultIdentityStore.setRelationshipProperties(resolveInvocationContext(defaultIdentityStore, ctx), relationship, properties);
   }

   public void removeRelationshipProperties(IdentityStoreInvocationContext ctx, IdentityObjectRelationship relationship, Set<String> properties) throws IdentityException, OperationNotSupportedException
   {
      IdentityStore fromStore = resolveIdentityStore(relationship.getFromIdentityObject());
      IdentityStore toStore = resolveIdentityStore(relationship.getToIdentityObject());

      if (fromStore == toStore && toStore.getSupportedFeatures().isNamedRelationshipsSupported())
      {
         fromStore.removeRelationshipProperties(resolveInvocationContext(fromStore, ctx), relationship, properties);
         return;
      }

      defaultIdentityStore.removeRelationshipProperties(resolveInvocationContext(defaultIdentityStore, ctx), relationship, properties);
   }

   public boolean validateCredential(IdentityStoreInvocationContext ctx, IdentityObject identityObject, IdentityObjectCredential credential) throws IdentityException
   {
      IdentityStore toStore = resolveIdentityStore(identityObject);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(toStore, ctx);

      return toStore.validateCredential(targetCtx, identityObject, credential);
   }

   public void updateCredential(IdentityStoreInvocationContext ctx, IdentityObject identityObject, IdentityObjectCredential credential) throws IdentityException
   {
      IdentityStore toStore = resolveIdentityStore(identityObject);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(toStore, ctx);

      toStore.updateCredential(targetCtx, identityObject, credential);
   }


   public Set<String> getSupportedAttributeNames(IdentityStoreInvocationContext invocationContext, IdentityObjectType identityType) throws IdentityException
   {
      Set<String> results;

      IdentityStore toStore = resolveIdentityStore(identityType);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(toStore, invocationContext);

      results = toStore.getSupportedAttributeNames(targetCtx, identityType);

      if (toStore != defaultAttributeStore)
      {
         IdentityStoreInvocationContext defaultCtx = resolveInvocationContext(defaultAttributeStore, invocationContext);

         results.addAll(defaultAttributeStore.getSupportedAttributeNames(defaultCtx, identityType));
      }

      return results;
   }

   public Map<String, IdentityObjectAttributeMetaData> getAttributesMetaData(IdentityStoreInvocationContext invocationContext,
                                                                            IdentityObjectType identityObjectType)
   {

      IdentityStore targetStore = resolveIdentityStore(identityObjectType);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(targetStore, invocationContext);

      Map<String, IdentityObjectAttributeMetaData> mdMap = new HashMap<String, IdentityObjectAttributeMetaData>();
      mdMap.putAll(targetStore.getAttributesMetaData(targetCtx, identityObjectType));

      if (targetStore != defaultAttributeStore)
      {
         IdentityStoreInvocationContext defaultCtx = resolveInvocationContext(defaultAttributeStore, invocationContext);

         Map<String, IdentityObjectAttributeMetaData> defaultMDMap = defaultAttributeStore.getAttributesMetaData(defaultCtx, identityObjectType);


         // put all missing attribute MD from default store
         if (defaultMDMap != null)
         {
            for (Map.Entry<String, IdentityObjectAttributeMetaData> entry : defaultMDMap.entrySet())
            {
               if (!mdMap.containsKey(entry.getKey()))
               {
                  mdMap.put(entry.getKey(), entry.getValue());
               }
            }
         }
      }

      return mdMap;
   }

   public IdentityObjectAttribute getAttribute(IdentityStoreInvocationContext invocationContext, IdentityObject identity, String name) throws IdentityException
   {
      IdentityObjectAttribute result;

      IdentityStore toStore = resolveIdentityStore(identity);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(toStore, invocationContext);

      result = toStore.getAttribute(targetCtx, identity, name);

      if (result == null && toStore != defaultAttributeStore)
      {
         IdentityStoreInvocationContext defaultCtx = resolveInvocationContext(defaultAttributeStore, invocationContext);

         result = defaultAttributeStore.getAttribute(defaultCtx, identity, name);
      }

      return result;
   }

   public Map<String, IdentityObjectAttribute> getAttributes(IdentityStoreInvocationContext invocationContext, IdentityObject identity) throws IdentityException
   {
      Map<String, IdentityObjectAttribute> results;

      IdentityStore toStore = resolveIdentityStore(identity);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(toStore, invocationContext);

      results = toStore.getAttributes(targetCtx, identity);

      if (toStore != defaultAttributeStore)
      {
         IdentityStoreInvocationContext defaultCtx = resolveInvocationContext(defaultAttributeStore, invocationContext);

         Map<String, IdentityObjectAttribute> defaultAttrs = defaultAttributeStore.getAttributes(defaultCtx, identity);

         // Add only those attributes which are missing - don't overwrite or merge existing values
         for (Map.Entry<String, IdentityObjectAttribute> entry : defaultAttrs.entrySet())
         {
            if (!results.keySet().contains(entry.getKey()))
            {
               results.put(entry.getKey(), entry.getValue());
            }
         }
      }

      return results;
   }

   public void updateAttributes(IdentityStoreInvocationContext invocationCtx, IdentityObject identity, IdentityObjectAttribute[] attributes) throws IdentityException
   {
      ArrayList<IdentityObjectAttribute> filteredAttrs = new ArrayList<IdentityObjectAttribute>();
      ArrayList<IdentityObjectAttribute> leftAttrs = new ArrayList<IdentityObjectAttribute>();

      IdentityObjectAttribute[] attributesToAdd = null;

      IdentityStore toStore = resolveIdentityStore(identity);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(toStore, invocationCtx);

      // Put supported attrs to the main store
      if (toStore != defaultAttributeStore)
      {
         Set<String> supportedAttrs = toStore.getSupportedAttributeNames(targetCtx, identity.getIdentityType());

         // Filter out supported and not supported attributes
         for (IdentityObjectAttribute entry : attributes)
         {
            if (supportedAttrs.contains(entry.getName()))
            {
               filteredAttrs.add(entry);
            }
            else
            {
               leftAttrs.add(entry);
            }
         }

         toStore.updateAttributes(targetCtx, identity, filteredAttrs.toArray(new IdentityObjectAttribute[filteredAttrs.size()]));

         attributesToAdd = leftAttrs.toArray(new IdentityObjectAttribute[leftAttrs.size()]);

      }
      else
      {
         attributesToAdd = attributes;
      }

      IdentityStoreInvocationContext defaultCtx = resolveInvocationContext(defaultAttributeStore, invocationCtx);

      if (isAllowNotDefinedAttributes())
      {
         defaultAttributeStore.updateAttributes(defaultCtx, identity, attributesToAdd);
      }
      else
      {
         Set<String> supportedAttrs = defaultAttributeStore.getSupportedAttributeNames(defaultCtx, identity.getIdentityType());
         for (IdentityObjectAttribute entry : leftAttrs)
         {
            if (!supportedAttrs.contains(entry.getName()))
            {
               throw new IdentityException("Cannot update not defined attribute. Use '"
                  + ALLOW_NOT_DEFINED_ATTRIBUTES + "' option to pass such attributes to default IdentityStore anyway." +
                  "Attribute name: " + entry.getName());
            }
         }
         defaultAttributeStore.updateAttributes(defaultCtx, identity, attributesToAdd);
      }

   }

   public void addAttributes(IdentityStoreInvocationContext invocationCtx, IdentityObject identity, IdentityObjectAttribute[] attributes) throws IdentityException
   {

      ArrayList<IdentityObjectAttribute> filteredAttrs = new ArrayList<IdentityObjectAttribute>();
      ArrayList<IdentityObjectAttribute> leftAttrs = new ArrayList<IdentityObjectAttribute>();
      IdentityObjectAttribute[] attributesToAdd = null;

      IdentityStore toStore = resolveIdentityStore(identity);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(toStore, invocationCtx);

      // Put supported attrs to the main store
      if (toStore != defaultAttributeStore)
      {
         Set<String> supportedAttrs = toStore.getSupportedAttributeNames(targetCtx, identity.getIdentityType());

         // Filter out supported and not supported attributes
         for (IdentityObjectAttribute entry : attributes)
         {
            if (supportedAttrs.contains(entry.getName()))
            {
               filteredAttrs.add(entry);
            }
            else
            {
               leftAttrs.add(entry);
            }
         }

         toStore.addAttributes(targetCtx, identity, filteredAttrs.toArray(new IdentityObjectAttribute[filteredAttrs.size()]));

         attributesToAdd = leftAttrs.toArray(new IdentityObjectAttribute[leftAttrs.size()]);


      }
      else
      {
         attributesToAdd = attributes;
      }

      IdentityStoreInvocationContext defaultCtx = resolveInvocationContext(defaultAttributeStore, invocationCtx);

      if (isAllowNotDefinedAttributes())
      {
         defaultAttributeStore.addAttributes(defaultCtx, identity, attributesToAdd);
      }
      else
      {
         Set<String> supportedAttrs = defaultAttributeStore.getSupportedAttributeNames(defaultCtx, identity.getIdentityType());
         for (IdentityObjectAttribute entry : attributesToAdd)
         {
            // if we hit some unsupported attribute at this stage that we cannot store...
            if (!supportedAttrs.contains(entry.getName()))
            {
               throw new IdentityException("Cannot add not defined attribute. Use '"
                  + ALLOW_NOT_DEFINED_ATTRIBUTES + "' option to pass such attributes to default IdentityStore anyway." +
                  "Attribute name: " + entry.getName());
            }

         }
         defaultAttributeStore.addAttributes(defaultCtx, identity, attributesToAdd);
      }
      
   }

   public void removeAttributes(IdentityStoreInvocationContext invocationCtx, IdentityObject identity, String[] attributes) throws IdentityException
   {
      List<String> filteredAttrs = new LinkedList<String>();
      List<String> leftAttrs = new LinkedList<String>();

      IdentityStore toStore = resolveIdentityStore(identity);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(toStore, invocationCtx);

      // Put supported attrs to the main store
      if (toStore != defaultAttributeStore)
      {
         Set<String> supportedAttrs = toStore.getSupportedAttributeNames(targetCtx, identity.getIdentityType());

         // Filter out supported and not supported attributes
         for (String name : attributes)
         {
            if (supportedAttrs.contains(name))
            {
               filteredAttrs.add(name);
            }
            else
            {
               leftAttrs.add(name);
            }
         }

         toStore.removeAttributes(targetCtx, identity, filteredAttrs.toArray(new String[filteredAttrs.size()]));


      }
      else
      {
         leftAttrs = Arrays.asList(attributes);
      }

      IdentityStoreInvocationContext defaultCtx = resolveInvocationContext(defaultAttributeStore, invocationCtx);

      if (isAllowNotDefinedAttributes())
      {
         defaultAttributeStore.removeAttributes(defaultCtx, identity, leftAttrs.toArray(new String[leftAttrs.size()]));
      }
      else
      {
         Set<String> supportedAttrs = defaultAttributeStore.getSupportedAttributeNames(defaultCtx, identity.getIdentityType());
         for (String name : leftAttrs)
         {
            if (!supportedAttrs.contains(name))
            {
               throw new IdentityException("Cannot remove not defined attribute. Use '"
                  + ALLOW_NOT_DEFINED_ATTRIBUTES + "' option to pass such attributes to default IdentityStore anyway." +
                  "Attribute name: " + name);
            }
         }
         defaultAttributeStore.removeAttributes(defaultCtx, identity, leftAttrs.toArray(new String[leftAttrs.size()]));
      }
   }

   public IdentityObject findIdentityObjectByUniqueAttribute(IdentityStoreInvocationContext invocationCtx, IdentityObjectType identityObjectType, IdentityObjectAttribute attribute) throws IdentityException
   {
      List<String> filteredAttrs = new LinkedList<String>();
      List<String> leftAttrs = new LinkedList<String>();

      IdentityStore toStore = resolveIdentityStore(identityObjectType);
      IdentityStoreInvocationContext targetCtx = resolveInvocationContext(toStore, invocationCtx);

      // Put supported attrs to the main store
      if (toStore != defaultAttributeStore)
      {
         Set<String> supportedAttrs = toStore.getSupportedAttributeNames(targetCtx, identityObjectType);

         if (supportedAttrs.contains(attribute.getName()))
         {
            return toStore.findIdentityObjectByUniqueAttribute(targetCtx, identityObjectType, attribute);
         }
      }

      IdentityStoreInvocationContext defaultCtx = resolveInvocationContext(defaultAttributeStore, invocationCtx);

      if (isAllowNotDefinedAttributes())
      {
         defaultAttributeStore.findIdentityObjectByUniqueAttribute(defaultCtx, identityObjectType, attribute);
      }
      else
      {
         Set<String> supportedAttrs = defaultAttributeStore.getSupportedAttributeNames(defaultCtx, identityObjectType);
         if (supportedAttrs.contains(attribute.getName()))
         {
            return toStore.findIdentityObjectByUniqueAttribute(defaultCtx, identityObjectType, attribute);
         }
      }

      return null;
   }

   private void sortByName(List<IdentityObject> objects, final boolean ascending)
   {
      Collections.sort(objects, new Comparator<IdentityObject>(){
         public int compare(IdentityObject o1, IdentityObject o2)
         {
            if (ascending)
            {
               return o1.getName().compareTo(o2.getName());
            }
            else
            {
               return o2.getName().compareTo(o1.getName());
            }
         }
      });
   }

   //TODO: other way
   private List<IdentityObject> cutPageFromResults(List<IdentityObject> objects, IdentityObjectSearchCriteria criteria)
   {
      List<IdentityObject> results = new LinkedList<IdentityObject>();
      for (int i = criteria.getFirstResult(); i < criteria.getFirstResult() + criteria.getMaxResults(); i++)
      {
         if (i < objects.size())
         {
            results.add(objects.get(i));
         }
      }
      return results;
   }

   public boolean isAllowNotDefinedAttributes()
   {
      return allowNotDefinedAttributes;
   }



}
