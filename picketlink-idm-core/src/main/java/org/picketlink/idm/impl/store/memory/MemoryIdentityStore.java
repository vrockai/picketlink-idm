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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.types.SimpleIdentityObject;
import org.picketlink.idm.spi.configuration.IdentityStoreConfigurationContext;
import org.picketlink.idm.spi.configuration.metadata.IdentityObjectAttributeMetaData;
import org.picketlink.idm.spi.exception.OperationNotSupportedException;
import org.picketlink.idm.spi.model.IdentityObject;
import org.picketlink.idm.spi.model.IdentityObjectAttribute;
import org.picketlink.idm.spi.model.IdentityObjectCredential;
import org.picketlink.idm.spi.model.IdentityObjectCredentialType;
import org.picketlink.idm.spi.model.IdentityObjectRelationship;
import org.picketlink.idm.spi.model.IdentityObjectRelationshipType;
import org.picketlink.idm.spi.model.IdentityObjectType;
import org.picketlink.idm.spi.model.IdentityObjectType.TYPE;
import org.picketlink.idm.spi.search.IdentityObjectSearchCriteria;
import org.picketlink.idm.spi.store.FeaturesMetaData;
import org.picketlink.idm.spi.store.IdentityObjectSearchCriteriaType;
import org.picketlink.idm.spi.store.IdentityStore;
import org.picketlink.idm.spi.store.IdentityStoreInvocationContext;
import org.picketlink.idm.spi.store.IdentityStoreSession;

/**
 * An implementation of {@link IdentityStore} that resides in memory
 * @author Anil.Saldhana@redhat.com
 * @since Feb 14, 2011
 */
public class MemoryIdentityStore implements IdentityStore, Serializable
{ 
   private static final long serialVersionUID = 1L;
   private String id;
   
   protected Map<String,IdentityObject> users = new HashMap<String,IdentityObject>();
   
   protected Map<String, IdentityObject> roles = new HashMap<String,IdentityObject>();
   protected Map<String, IdentityObject> groups = new HashMap<String,IdentityObject>();
   
   protected Map<String, Set<IdentityObjectAttribute>> attributes = new HashMap<String, Set<IdentityObjectAttribute>>();
   /**
    * General hashmap that is keyed by relationship name.
    */
   protected Map<String, List<IdentityObjectRelationship>> relationships = new HashMap<String, List<IdentityObjectRelationship>>();
   
   protected Set<String> relationshipNames = new TreeSet<String>();
   
   public MemoryIdentityStore( String id )
   {
      this.id = id;
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
      Map<String, IdentityObjectAttribute> map = new HashMap<String, IdentityObjectAttribute>();
      
      Set<IdentityObjectAttribute> attrs = attributes.get( identity.getName());
      if( attrs != null )
      {
         for( IdentityObjectAttribute ioa: attrs )
         {
            map.put(ioa.getName(), ioa );
         }
      }
      return map;
   }

   public IdentityObjectAttribute getAttribute(IdentityStoreInvocationContext invocationContext,
         IdentityObject identity, String name) throws IdentityException
   {   
      throw new RuntimeException( "NYI" );
   }

   public void updateAttributes(IdentityStoreInvocationContext invocationCtx, IdentityObject identity,
         IdentityObjectAttribute[] attributes) throws IdentityException
   { 
      throw new RuntimeException( "NYI" );
   }

   public void addAttributes(IdentityStoreInvocationContext invocationCtx, IdentityObject identity,
         IdentityObjectAttribute[] attributes) throws IdentityException
   { 
      Set<IdentityObjectAttribute> set = this.attributes.get( identity.getName() );
      if( set == null)
      {
         set = new HashSet<IdentityObjectAttribute>();
         this.attributes.put( identity.getName(), set );
      }
      set.addAll( Arrays.asList( attributes ));
   }

   public void removeAttributes(IdentityStoreInvocationContext invocationCtx, IdentityObject identity,
         String[] attributeNames) throws IdentityException
   {
      throw new RuntimeException( "NYI" );
   }

   public IdentityObject findIdentityObjectByUniqueAttribute(IdentityStoreInvocationContext invocationCtx,
         IdentityObjectType identityObjectType, IdentityObjectAttribute attribute) throws IdentityException
   {   
      throw new RuntimeException( "NYI" );
   }

   public IdentityStoreSession createIdentityStoreSession() throws IdentityException
   {   
      throw new RuntimeException( "NYI" );
   }

   public IdentityStoreSession createIdentityStoreSession(Map<String, Object> sessionOptions) throws IdentityException
   {   
      throw new RuntimeException( "NYI" );
   }

   public void bootstrap(IdentityStoreConfigurationContext configurationContext) throws IdentityException
   {  
   }

   public String getId()
   {
      return id;
   }

   public FeaturesMetaData getSupportedFeatures()
   { 
      return new FeaturesMetaData()
      {
         public boolean isSearchCriteriaTypeSupported(IdentityObjectType identityObjectType,
               IdentityObjectSearchCriteriaType storeSearchConstraint)
         { 
            return true;
         }
         
         public boolean isRoleNameSearchCriteriaTypeSupported(IdentityObjectSearchCriteriaType constraint)
         {  
            return true;
         }
         
         public boolean isRelationshipTypeSupported(IdentityObjectType fromType, IdentityObjectType toType,
               IdentityObjectRelationshipType relationshipType) throws IdentityException
         {   
            return true;
         }
         
         public boolean isRelationshipPropertiesSupported()
         { 
            return true;
         }
         
         public boolean isRelationshipNameAddRemoveSupported()
         { 
            return true;
         }
         
         public boolean isNamedRelationshipsSupported()
         { 
            return true;
         }
         
         public boolean isIdentityObjectTypeSupported(IdentityObjectType identityObjectType)
         { 
            return true;
         }
         
         public boolean isIdentityObjectAddRemoveSupported(IdentityObjectType objectType)
         { 
            return true;
         }
         
         public boolean isCredentialSupported(IdentityObjectType identityObjectType,
               IdentityObjectCredentialType credentialType)
         {   
            return true;
         }
         
         public Set<String> getSupportedRelationshipTypes()
         {   
            return null;
         }
         
         public Set<String> getSupportedIdentityObjectTypes()
         { 
            return null;
         }
      }; 
   }

   public IdentityObject createIdentityObject(IdentityStoreInvocationContext invocationCtx, String name,
         IdentityObjectType identityObjectType) throws IdentityException
   { 
      if( identityObjectType == null )
         throw new IllegalArgumentException( "Identity Object Type is null" );
      
      IdentityObject io = null;
      
      String identityObjectTypeName =  identityObjectType.getName();
      
      TYPE type = identityObjectType.getType();
      
      if( type == TYPE.GROUP )
      {
         io = groups.get(identityObjectTypeName);
         if( io == null )
         {
            io = new SimpleIdentityObject( name, identityObjectType );
            groups.put(name, io);
         }  
      }
      else if( type == TYPE.USER )
      {
         io = users.get(identityObjectTypeName);
         if( io ==null)
         {
            io = new SimpleIdentityObject( name, identityObjectType );
            users.put( name, io ); 
         }
      }
      else
      {
         io = roles.get(identityObjectTypeName);
         if( io == null )
         {
            io = new SimpleIdentityObject( name, identityObjectType );
            roles.put(identityObjectTypeName, io);
         }
      }
      /*if( identityObjectTypeName.equals( "ORGANIZATION" ) || identityObjectTypeName.equals( "DIVISION" )
            || identityObjectTypeName.equals( "DEPARTMENT") ||  identityObjectTypeName.equals( "ORGANIZATION_UNIT")
            || identityObjectTypeName.equals( "PROJECT" ) || identityObjectTypeName.equals( "PEOPLE" ))
      {
         io =  generalBucket.get(name);
         
         if( io == null )
         {
            io = new SimpleIdentityObject( name, identityObjectType );
            generalBucket.put(name, io);
         }  
      } 
      else if( identityObjectTypeName.equals( "USER" ))
      {
         io = users.get( name );
         if( io == null )
         {
            io = new SimpleIdentityObject( name, identityObjectType );
            users.put(name, io );
         }
      }
      else 
         throw new RuntimeException( "Unknown type : " + identityObjectTypeName );*/
      return io;
   }

   public IdentityObject createIdentityObject(IdentityStoreInvocationContext invocationCtx, String name,
         IdentityObjectType identityObjectType, Map<String, String[]> attributes) throws IdentityException
   {    
      throw new RuntimeException( "NYI" );
   }

   public void removeIdentityObject(IdentityStoreInvocationContext invocationCtx, IdentityObject identity)
         throws IdentityException
   {
      throw new RuntimeException( "NYI" );
   }

   public int getIdentityObjectsCount(IdentityStoreInvocationContext invocationCtx, IdentityObjectType identityType)
         throws IdentityException
   {
      throw new RuntimeException( "NYI" );
   }

   public IdentityObject findIdentityObject(IdentityStoreInvocationContext invocationContext, String name,
         IdentityObjectType identityObjectType) throws IdentityException
   { 
      throw new RuntimeException( "NYI" );
   }

   public IdentityObject findIdentityObject(IdentityStoreInvocationContext invocationContext, String id)
         throws IdentityException
   { 
      throw new RuntimeException( "NYI" );
   }

   public Collection<IdentityObject> findIdentityObject(IdentityStoreInvocationContext invocationCtx,
         IdentityObjectType identityType, IdentityObjectSearchCriteria criteria) throws IdentityException
   { 
      throw new RuntimeException( "NYI" );
   }

   public Collection<IdentityObject> findIdentityObject(IdentityStoreInvocationContext invocationCxt,
         IdentityObject identity, IdentityObjectRelationshipType relationshipType, boolean parent,
         IdentityObjectSearchCriteria criteria) throws IdentityException
   { 
      Collection<IdentityObject> result = new HashSet<IdentityObject>();
      
      String relationshipName = relationshipType.getName();
      List<IdentityObjectRelationship> rels = relationships.get(relationshipName);
      if( rels != null )
      {
         for( IdentityObjectRelationship rel: rels )
         {
            String identityName = identity.getName();
            IdentityObject from = rel.getFromIdentityObject();
            IdentityObject to = rel.getToIdentityObject();
            
            if( from.getName().equals(identityName) || to.getName().equals(identityName) )
            {
               result.add( from ); 
               result.add( to);
            }
         }
      }
      
      /*TYPE type = identity.getIdentityType().getType();
      if( type == TYPE.GROUP )
      {
         IdentityObject io = groups.get( identity.getName() );
         System.out.println( io ); 
         
      }
      throw new RuntimeException( "NYI" );*/
      return result;
   }

   public IdentityObjectRelationship createRelationship(IdentityStoreInvocationContext invocationCxt,
         IdentityObject fromIdentity, IdentityObject toIdentity, IdentityObjectRelationshipType relationshipType,
         String relationshipName, boolean createNames) throws IdentityException
   { 
      String relationshipTypeName = relationshipType.getName(); 
      
      List<IdentityObjectRelationship> rels;
      
      IdentityObjectRelationship  ior = new MemoryIdentityObjectRelationship(relationshipName, 
                                                      relationshipType, fromIdentity, toIdentity);
      
      if( ! relationships.containsKey(relationshipTypeName) )
      {
         rels = new ArrayList<IdentityObjectRelationship>();
         rels.add(ior); 
         relationships.put(relationshipTypeName, rels ); 
      }
      else
      {
         rels = relationships.get(relationshipTypeName);
         rels.add(ior); 
      }
      return ior;
   }

   public void removeRelationship(IdentityStoreInvocationContext invocationCxt, IdentityObject fromIdentity,
         IdentityObject toIdentity, IdentityObjectRelationshipType relationshipType, String relationshipName)
         throws IdentityException
   { 
      throw new RuntimeException( "NYI" );
   }

   public void removeRelationships(IdentityStoreInvocationContext invocationCtx, IdentityObject identity1,
         IdentityObject identity2, boolean named) throws IdentityException
   { 
      throw new RuntimeException( "NYI" );
   }

   public Set<IdentityObjectRelationship> resolveRelationships(IdentityStoreInvocationContext invocationCxt,
         IdentityObject fromIdentity, IdentityObject toIdentity, IdentityObjectRelationshipType relationshipType)
         throws IdentityException
   { 
      Set<IdentityObjectRelationship> set = new HashSet<IdentityObjectRelationship>();
      
      String relationshipTypeName = relationshipType.getName();
      List<IdentityObjectRelationship> rels = relationships.get(relationshipTypeName);
      if( rels != null )
      {
         for( IdentityObjectRelationship ior : rels )
         {
            IdentityObject iroFrom = ior.getFromIdentityObject();
            IdentityObject iroTo = ior.getToIdentityObject();
            if( iroFrom.getName().equals( fromIdentity.getName() ) && iroTo.getName().equals( toIdentity.getName() ) )
               set.add(ior);
         } 
      }
      return set;
   }

   public Set<IdentityObjectRelationship> resolveRelationships(IdentityStoreInvocationContext invocationCxt,
         IdentityObject identity, IdentityObjectRelationshipType relationshipType, boolean parent, boolean named,
         String name) throws IdentityException
   {   
      throw new RuntimeException( "NYI" );
   }

   public String createRelationshipName(IdentityStoreInvocationContext ctx, String name) throws IdentityException,
         OperationNotSupportedException
   {  
      relationshipNames.add(name);
      return name;
   }

   public String removeRelationshipName(IdentityStoreInvocationContext ctx, String name) throws IdentityException,
         OperationNotSupportedException
   {   
      throw new RuntimeException( "NYI" );
   }

   public Map<String, String> getRelationshipNameProperties(IdentityStoreInvocationContext ctx, String name)
         throws IdentityException, OperationNotSupportedException
   {   
      throw new RuntimeException( "NYI" );
   }

   public void setRelationshipNameProperties(IdentityStoreInvocationContext ctx, String name,
         Map<String, String> properties) throws IdentityException, OperationNotSupportedException
   { 
      throw new RuntimeException( "NYI" );
   }

   public void removeRelationshipNameProperties(IdentityStoreInvocationContext ctx, String name, Set<String> properties)
         throws IdentityException, OperationNotSupportedException
   {
      throw new RuntimeException( "NYI" );
   }

   public Map<String, String> getRelationshipProperties(IdentityStoreInvocationContext ctx,
         IdentityObjectRelationship relationship) throws IdentityException, OperationNotSupportedException
   {   
      throw new RuntimeException( "NYI" );
   }

   public void setRelationshipProperties(IdentityStoreInvocationContext ctx, IdentityObjectRelationship relationship,
         Map<String, String> properties) throws IdentityException, OperationNotSupportedException
   { 
      throw new RuntimeException( "NYI" );
   }

   public void removeRelationshipProperties(IdentityStoreInvocationContext ctx,
         IdentityObjectRelationship relationship, Set<String> properties) throws IdentityException,
         OperationNotSupportedException
   { 
      throw new RuntimeException( "NYI" );
   }

   public Set<String> getRelationshipNames(IdentityStoreInvocationContext ctx, IdentityObjectSearchCriteria criteria)
         throws IdentityException, OperationNotSupportedException
   {   
      return Collections.unmodifiableSet( relationshipNames );
   }

   public Set<String> getRelationshipNames(IdentityStoreInvocationContext ctx, IdentityObject identity,
         IdentityObjectSearchCriteria criteria) throws IdentityException, OperationNotSupportedException
   {    
      Set<String> result = new TreeSet<String>();
      
      String identityName = identity.getName();
      
      if( identityName == null )
         throw new IllegalStateException( "Identity Name null" );
      
      Set<String> keys =  relationships.keySet();
      for( String key : keys )
      {
         List<IdentityObjectRelationship> iors = relationships.get(key);
         for( IdentityObjectRelationship ior: iors )
         {
            if( ior.getName() == null )
               continue;
            
            String fromID = ior.getFromIdentityObject().getName();
            String toID = ior.getToIdentityObject().getName();
             
            if( fromID.equals( identityName) || toID.equals( identityName ))
                result.add(ior.getName());   
         }
      }
      return result;
   }

   public boolean validateCredential(IdentityStoreInvocationContext ctx, IdentityObject identityObject,
         IdentityObjectCredential credential) throws IdentityException
   {   
      return false;
   }

   public void updateCredential(IdentityStoreInvocationContext ctx, IdentityObject identityObject,
         IdentityObjectCredential credential) throws IdentityException
   { 
      throw new RuntimeException( "NYI" );
   }
}