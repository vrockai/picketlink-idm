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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.core.factories.IdentityFactory;
import org.picketlink.idm.spi.configuration.IdentityRepositoryConfigurationContext;
import org.picketlink.idm.spi.configuration.IdentityStoreConfigurationContext;
import org.picketlink.idm.spi.configuration.metadata.IdentityConfigurationMetaData;
import org.picketlink.idm.spi.configuration.metadata.IdentityObjectAttributeMetaData;
import org.picketlink.idm.spi.configuration.metadata.IdentityRepositoryConfigurationMetaData;
import org.picketlink.idm.spi.configuration.metadata.IdentityStoreConfigurationMetaData;
import org.picketlink.idm.spi.exception.OperationNotSupportedException;
import org.picketlink.idm.spi.model.IdentityObject;
import org.picketlink.idm.spi.model.IdentityObjectAttribute;
import org.picketlink.idm.spi.model.IdentityObjectCredential;
import org.picketlink.idm.spi.model.IdentityObjectRelationship;
import org.picketlink.idm.spi.model.IdentityObjectRelationshipType;
import org.picketlink.idm.spi.model.IdentityObjectType;
import org.picketlink.idm.spi.repository.IdentityStoreRepository;
import org.picketlink.idm.spi.search.IdentityObjectSearchCriteria;
import org.picketlink.idm.spi.store.AttributeStore;
import org.picketlink.idm.spi.store.FeaturesMetaData;
import org.picketlink.idm.spi.store.IdentityObjectSearchCriteriaType;
import org.picketlink.idm.spi.store.IdentityStore;
import org.picketlink.idm.spi.store.IdentityStoreInvocationContext;
import org.picketlink.idm.spi.store.IdentityStoreSession;

/**
 * A {@link IdentityStoreRepository} that resides in memory
 * @author Anil.Saldhana@redhat.com
 * @since Feb 14, 2011
 */
public class MemoryIdentityStoreRepository implements IdentityStoreRepository
{ 
   private static final long serialVersionUID = 1L;

   protected String id = null;
   
   protected IdentityStore store;
   
   protected IdentityConfigurationMetaData md;
   
   protected IdentityRepositoryConfigurationMetaData repoMD = null;
   
   protected IdentityStoreConfigurationMetaData storeMD = null;
   
   private static Set<IdentityObjectSearchCriteriaType> supportedIdentityObjectSearchCriteria =
      new HashSet<IdentityObjectSearchCriteriaType>();

   private static Set<String> supportedCredentialTypes = new HashSet<String>();
   
   public static final String CREDENTIAL_TYPE_PASSWORD = "PASSWORD";

   public static final String CREDENTIAL_TYPE_BINARY = "BINARY";
   
   static {
      // List all supported criteria classes

      supportedIdentityObjectSearchCriteria.add(IdentityObjectSearchCriteriaType.ATTRIBUTE_FILTER);
      supportedIdentityObjectSearchCriteria.add(IdentityObjectSearchCriteriaType.NAME_FILTER);
      supportedIdentityObjectSearchCriteria.add(IdentityObjectSearchCriteriaType.PAGE);
      supportedIdentityObjectSearchCriteria.add(IdentityObjectSearchCriteriaType.SORT);

      // credential types supported by this impl
      supportedCredentialTypes.add(CREDENTIAL_TYPE_PASSWORD);
      supportedCredentialTypes.add(CREDENTIAL_TYPE_BINARY);

   }

   
   public MemoryIdentityStoreRepository( String id )
   {
      this.id = id;
      store = new MemoryIdentityStore(id);
   }
    
   public String getId()
   {
      return id;
   }

   public FeaturesMetaData getSupportedFeatures()
   {   
      return store.getSupportedFeatures();
   }

   public IdentityObject createIdentityObject(IdentityStoreInvocationContext invocationCtx, String name,
         IdentityObjectType identityObjectType) throws IdentityException
   {    
      return store.createIdentityObject(invocationCtx, name, identityObjectType);
   }

   public IdentityObject createIdentityObject(IdentityStoreInvocationContext invocationCtx, String name,
         IdentityObjectType identityObjectType, Map<String, String[]> attributes) throws IdentityException
   {
      return store.createIdentityObject(invocationCtx, name, identityObjectType, attributes ); 
   }

   public void removeIdentityObject(IdentityStoreInvocationContext invocationCtx, IdentityObject identity)
         throws IdentityException
   { 
      store.removeIdentityObject(invocationCtx, identity);
   }

   public int getIdentityObjectsCount(IdentityStoreInvocationContext invocationCtx, IdentityObjectType identityType)
         throws IdentityException
   {   
      return store.getIdentityObjectsCount(invocationCtx, identityType);
   }

   public IdentityObject findIdentityObject(IdentityStoreInvocationContext invocationContext, String name,
         IdentityObjectType identityObjectType) throws IdentityException
   {  
      return store.findIdentityObject(invocationContext, name, identityObjectType ); 
   }

   public IdentityObject findIdentityObject(IdentityStoreInvocationContext invocationContext, String id)
         throws IdentityException
   {  
      return store.findIdentityObject(invocationContext, id); 
   }

   public Collection<IdentityObject> findIdentityObject(IdentityStoreInvocationContext invocationCtx,
         IdentityObjectType identityType, IdentityObjectSearchCriteria criteria) throws IdentityException
   {  
      return store.findIdentityObject(invocationCtx, identityType, criteria);
   }

   public Collection<IdentityObject> findIdentityObject(IdentityStoreInvocationContext invocationCxt,
         IdentityObject identity, IdentityObjectRelationshipType relationshipType, boolean parent,
         IdentityObjectSearchCriteria criteria) throws IdentityException
   {   
      return store.findIdentityObject(invocationCxt, identity, relationshipType, parent, criteria);
   }

   public IdentityObjectRelationship createRelationship(IdentityStoreInvocationContext invocationCxt,
         IdentityObject fromIdentity, IdentityObject toIdentity, IdentityObjectRelationshipType relationshipType,
         String relationshipName, boolean createNames) throws IdentityException
   {    
      return store.createRelationship(invocationCxt, fromIdentity, toIdentity, relationshipType, relationshipName, createNames);
   }

   public void removeRelationship(IdentityStoreInvocationContext invocationCxt, IdentityObject fromIdentity,
         IdentityObject toIdentity, IdentityObjectRelationshipType relationshipType, String relationshipName)
         throws IdentityException
   { 
      store.removeRelationship(invocationCxt, fromIdentity, toIdentity, relationshipType, relationshipName);
   }

   public void removeRelationships(IdentityStoreInvocationContext invocationCtx, IdentityObject identity1,
         IdentityObject identity2, boolean named) throws IdentityException
   { 
      store.removeRelationships(invocationCtx, identity1, identity2, named);
   }

   public Set<IdentityObjectRelationship> resolveRelationships(IdentityStoreInvocationContext invocationCxt,
         IdentityObject fromIdentity, IdentityObject toIdentity, IdentityObjectRelationshipType relationshipType)
         throws IdentityException
   {   
      return store.resolveRelationships(invocationCxt, fromIdentity, toIdentity, relationshipType);
   }

   public Set<IdentityObjectRelationship> resolveRelationships(IdentityStoreInvocationContext invocationCxt,
         IdentityObject identity, IdentityObjectRelationshipType relationshipType, boolean parent, boolean named,
         String name) throws IdentityException
   {    
      return store.resolveRelationships(invocationCxt, identity, relationshipType, parent, named, name );
   }

   public String createRelationshipName(IdentityStoreInvocationContext ctx, String name) throws IdentityException,
         OperationNotSupportedException
   {  
      return store.createRelationshipName(ctx, name); 
   }

   public String removeRelationshipName(IdentityStoreInvocationContext ctx, String name) throws IdentityException,
         OperationNotSupportedException
   {   
      return store.removeRelationshipName(ctx, name);
   }

   public Map<String, String> getRelationshipNameProperties(IdentityStoreInvocationContext ctx, String name)
         throws IdentityException, OperationNotSupportedException
   {  
      return store.getRelationshipNameProperties(ctx, name); 
   }

   public void setRelationshipNameProperties(IdentityStoreInvocationContext ctx, String name,
         Map<String, String> properties) throws IdentityException, OperationNotSupportedException
   { 
      store.setRelationshipNameProperties(ctx, name, properties);
   }

   public void removeRelationshipNameProperties(IdentityStoreInvocationContext ctx, String name, Set<String> properties)
         throws IdentityException, OperationNotSupportedException
   { 
      store.removeRelationshipNameProperties(ctx, name, properties);
   }

   public Map<String, String> getRelationshipProperties(IdentityStoreInvocationContext ctx,
         IdentityObjectRelationship relationship) throws IdentityException, OperationNotSupportedException
   {   
      throw new RuntimeException( "NYI" );
   }

   public void setRelationshipProperties(IdentityStoreInvocationContext ctx, IdentityObjectRelationship relationship,
         Map<String, String> properties) throws IdentityException, OperationNotSupportedException
   {
      

   }

   public void removeRelationshipProperties(IdentityStoreInvocationContext ctx,
         IdentityObjectRelationship relationship, Set<String> properties) throws IdentityException,
         OperationNotSupportedException
   { 
   }

   public Set<String> getRelationshipNames(IdentityStoreInvocationContext ctx, IdentityObjectSearchCriteria criteria)
         throws IdentityException, OperationNotSupportedException
   {   
      
      throw new RuntimeException( "NYI" );
   }

   public Set<String> getRelationshipNames(IdentityStoreInvocationContext ctx, IdentityObject identity,
         IdentityObjectSearchCriteria criteria) throws IdentityException, OperationNotSupportedException
   {    
      return store.getRelationshipNames(ctx, identity, criteria);
   }

   public boolean validateCredential(IdentityStoreInvocationContext ctx, IdentityObject identityObject,
         IdentityObjectCredential credential) throws IdentityException
   {   
      return false;
   }

   public void updateCredential(IdentityStoreInvocationContext ctx, IdentityObject identityObject,
         IdentityObjectCredential credential) throws IdentityException
   { 
   }

   public Set<String> getSupportedAttributeNames(IdentityStoreInvocationContext invocationContext,
         IdentityObjectType identityType) throws IdentityException
   {   
      
      throw new RuntimeException( "NYI" );
   }

   public Map<String, IdentityObjectAttributeMetaData> getAttributesMetaData(
         IdentityStoreInvocationContext invocationContext, IdentityObjectType identityType)
   {   
      
      throw new RuntimeException( "NYI" );
   }

   public Map<String, IdentityObjectAttribute> getAttributes(IdentityStoreInvocationContext invocationContext,
         IdentityObject identity) throws IdentityException
   {   
      return store.getAttributes( invocationContext, identity);
   }

   public IdentityObjectAttribute getAttribute(IdentityStoreInvocationContext invocationContext,
         IdentityObject identity, String name) throws IdentityException
   {   
      
      throw new RuntimeException( "NYI" );
   }

   public void updateAttributes(IdentityStoreInvocationContext invocationCtx, IdentityObject identity,
         IdentityObjectAttribute[] attributes) throws IdentityException
   { 
   }

   public void addAttributes(IdentityStoreInvocationContext invocationCtx, IdentityObject identity,
         IdentityObjectAttribute[] attributes) throws IdentityException
   {   
      store.addAttributes(  invocationCtx,   identity,  attributes);
   }

   public void removeAttributes(IdentityStoreInvocationContext invocationCtx, IdentityObject identity,
         String[] attributeNames) throws IdentityException
   { 
      store.removeAttributes(invocationCtx, identity, attributeNames);
   }

   public IdentityObject findIdentityObjectByUniqueAttribute(IdentityStoreInvocationContext invocationCtx,
         IdentityObjectType identityObjectType, IdentityObjectAttribute attribute) throws IdentityException
   {  
      return store.findIdentityObjectByUniqueAttribute(invocationCtx, identityObjectType, attribute); 
   }

   public IdentityStoreSession createIdentityStoreSession() throws IdentityException
   {   
      return store.createIdentityStoreSession(); 
   }

   public IdentityStoreSession createIdentityStoreSession(Map<String, Object> sessionOptions) throws IdentityException
   { 
      return new MemoryIdentityStoreSession();
   }

   public void bootstrap(IdentityRepositoryConfigurationContext configurationContext,
         Map<String, IdentityStore> bootstrappedIdentityStores, Map<String, AttributeStore> bootstrappedAttributeStores)
         throws IdentityException
   { 
      repoMD = configurationContext.getRepositoryConfigurationMetaData(); 
      storeMD =  configurationContext.getConfigurationMetaData().getIdentityStores().get(0);
   }
   

   public void bootstrap(IdentityStoreConfigurationContext configurationContext) throws IdentityException
   { 
      storeMD = configurationContext.getStoreConfigurationMetaData();
   }

   public Set<IdentityStore> getConfiguredIdentityStores()
   {   
      
      throw new RuntimeException( "NYI" );
   }

   public Set<AttributeStore> getConfiguredAttributeStores()
   {   
      
      throw new RuntimeException( "NYI" );
   }

   public Map<String, IdentityStore> getIdentityStoreMappings()
   {   
      
      throw new RuntimeException( "NYI" );
   }

   public Map<String, AttributeStore> getAttributeStoreMappings()
   {   
      
      throw new RuntimeException( "NYI" );
   }

   public IdentityStore getIdentityStore(IdentityObjectType identityObjectType) throws IdentityException
   {   
      
      throw new RuntimeException( "NYI" );
   }

   public AttributeStore getAttributeStore(IdentityObjectType identityObjectType) throws IdentityException
   {   
      
      throw new RuntimeException( "NYI" );
   }
}