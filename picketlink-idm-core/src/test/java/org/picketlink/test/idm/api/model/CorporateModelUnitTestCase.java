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
package org.picketlink.test.idm.api.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.picketlink.idm.api.Attribute;
import org.picketlink.idm.api.AttributesManager;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.IdentitySessionFactory;
import org.picketlink.idm.api.PersistenceManager;
import org.picketlink.idm.api.RelationshipManager;
import org.picketlink.idm.api.RoleManager;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.User;
import org.picketlink.idm.api.cfg.IdentityConfiguration;
import org.picketlink.idm.common.p3p.P3PConstants;
import org.picketlink.idm.core.factories.IdentityFactory;
import org.picketlink.idm.impl.api.SimpleAttribute;

/**
 * Unit test a typical corporate model
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @author Anil.Saldhana@redhat.com
 * @since Feb 14, 2011
 */
public class CorporateModelUnitTestCase
{
   private String sampleOrganizationRealmName = "realm://memory";

   String configFileName = "configs/memory-config.xml";
   
   
   @Test
   public void testModel() throws Exception
   {
      IdentityConfiguration config = IdentityFactory.createConfiguration( configFileName );
      assertNotNull( config );
      IdentitySessionFactory sessionFactory = config.buildIdentitySessionFactory();
      assertNotNull( sessionFactory );    
      
      // GroupType

      String ORGANIZATION = "ORGANIZATION";
      String ORGANIZATION_UNIT = "ORGANIZATION_UNIT";
      String DIVISION = "DIVISION";
      String DEPARTMENT = "DEPARTMENT";
      String PROJECT = "PROJECT";
      String PEOPLE = "PEOPLE";
 

      IdentitySession session = sessionFactory .createIdentitySession( sampleOrganizationRealmName );
 

      begin();

      // Organization structure

      PersistenceManager persistenceManager = session.getPersistenceManager();
      Group rhOrg = persistenceManager.createGroup("RedHat", ORGANIZATION);

      Group jbossDivision = persistenceManager.createGroup("JBoss", DIVISION);
      Group rhelDivision = persistenceManager.createGroup("RHEL", DIVISION);

      RelationshipManager relationshipManager = session.getRelationshipManager();
      relationshipManager.associateGroups(rhOrg, jbossDivision);
      relationshipManager.associateGroups(rhOrg, rhelDivision);

      Group itDepartment = persistenceManager.createGroup("IT", DEPARTMENT);
      Group hrDepartment = persistenceManager.createGroup("HR", DEPARTMENT);

      relationshipManager.associateGroups(jbossDivision, itDepartment);
      relationshipManager.associateGroups(jbossDivision, hrDepartment);

      Group rndDepartment = persistenceManager.createGroup("RnD", DEPARTMENT); 

      relationshipManager.associateGroups(itDepartment, rndDepartment);

      Group projectsOU = persistenceManager.createGroup("Projects", ORGANIZATION_UNIT);
      Group commonFrameworksOU = persistenceManager.createGroup("Common Frameworks", ORGANIZATION_UNIT);

      relationshipManager.associateGroups(rndDepartment, projectsOU);

      // Projects

      Group portalProject = persistenceManager.createGroup("Portal", PROJECT);
      Group soaProject = persistenceManager.createGroup("SOA", PROJECT);
      Group jbpmProject = persistenceManager.createGroup("jBPM", PROJECT);
      Group seamProject = persistenceManager.createGroup("Seam", PROJECT);
      Group asProject = persistenceManager.createGroup("AS", PROJECT);
      Group securityProject = persistenceManager.createGroup("Security", PROJECT);

      relationshipManager.associateGroups(projectsOU, portalProject);
      relationshipManager.associateGroups(projectsOU, soaProject);
      relationshipManager.associateGroups(projectsOU, jbpmProject);
      relationshipManager.associateGroups(projectsOU, asProject);
      relationshipManager.associateGroups(projectsOU, seamProject);

      // Check...
      assertTrue(relationshipManager.isAssociated(projectsOU, portalProject));
      assertTrue(relationshipManager.isAssociated(projectsOU, soaProject));
      assertTrue(relationshipManager.isAssociated(projectsOU, jbpmProject));
      assertTrue(relationshipManager.isAssociated(projectsOU, asProject));
      assertTrue(relationshipManager.isAssociated(projectsOU, seamProject));

      // Portal is part of common frameworks
      relationshipManager.associateGroups(commonFrameworksOU, portalProject);

      // People

      Group employeesGroup = persistenceManager.createGroup("Employees", PEOPLE);

      // Management

      User theuteUser = persistenceManager.createUser("theute");
      User mlittleUser = persistenceManager.createUser("mlittle");
      User bgeorgesUser = persistenceManager.createUser("bgeorges");
      User asaldhanaUser = persistenceManager.createUser("asaldhana");
      User janderseUser = persistenceManager.createUser("janderse");

       // Portal Team

      User bdawidowUser = persistenceManager.createUser("bdawidow");
      User claprunUser = persistenceManager.createUser("claprun");
      User whalesUser = persistenceManager.createUser("whales");
      User sshahUser = persistenceManager.createUser("sshah");
      User mwringeUser = persistenceManager.createUser("mwringe");

      // Store as employees

      relationshipManager.associateUser(employeesGroup, theuteUser);
      relationshipManager.associateUser(employeesGroup, mlittleUser);
      relationshipManager.associateUser(employeesGroup, asaldhanaUser);
      relationshipManager.associateUser(employeesGroup, bdawidowUser);
      relationshipManager.associateUser(employeesGroup, claprunUser);
      relationshipManager.associateUser(employeesGroup, whalesUser);
      relationshipManager.associateUser(employeesGroup, sshahUser);
      relationshipManager.associateUser(employeesGroup, mwringeUser);

      // Portal team for management purposes

      Group portalTeamGroup = persistenceManager.createGroup("Portal Team", PEOPLE);
      relationshipManager.associateUser(portalTeamGroup, bdawidowUser);
      relationshipManager.associateUser(portalTeamGroup, claprunUser);
      relationshipManager.associateUser(portalTeamGroup, whalesUser);
      relationshipManager.associateUser(portalTeamGroup, sshahUser);
      relationshipManager.associateUser(portalTeamGroup, mwringeUser);

      // Portal team is under common frameworks

      relationshipManager.associateGroups(commonFrameworksOU, portalTeamGroup);

      // Role Types

      RoleManager roleManager = session.getRoleManager();
      RoleType developerRT = roleManager.createRoleType("Developer");
      RoleType managerRT = roleManager.createRoleType("Manager");
      RoleType leadDeveloperRT = roleManager.createRoleType("Lead Developer");
      RoleType productManagerRT = roleManager.createRoleType("Product Manager");

      // Assign roles

      // Common frameworks manager

      roleManager.createRole(managerRT, bgeorgesUser, commonFrameworksOU);

      // Portal developers

      roleManager.createRole(developerRT, theuteUser, portalProject);
      roleManager.createRole(developerRT, bdawidowUser, portalProject);
      roleManager.createRole(developerRT, claprunUser, portalProject);
      roleManager.createRole(developerRT, whalesUser, portalProject);
      roleManager.createRole(developerRT, sshahUser, portalProject);
      roleManager.createRole(developerRT, mwringeUser, portalProject);

      // Portal management
      roleManager.createRole(leadDeveloperRT, theuteUser, portalProject);
      roleManager.createRole(managerRT, theuteUser, portalTeamGroup);
      roleManager.createRole(productManagerRT, janderseUser, portalProject);

      // SOA

      roleManager.createRole(developerRT, mlittleUser, portalProject);
      roleManager.createRole(productManagerRT, mlittleUser, portalProject);

      // AS & Security

      roleManager.createRole(developerRT, asaldhanaUser, asProject);
      roleManager.createRole(developerRT, asaldhanaUser, securityProject);
      roleManager.createRole(leadDeveloperRT, asaldhanaUser, securityProject);


      // Check what RoleTypes has user theute
      Collection<RoleType> roleTypes = roleManager.findUserRoleTypes(theuteUser);
      assertEquals(3, roleTypes.size());
      assertTrue(roleTypes.contains(developerRT));
      assertTrue(roleTypes.contains(leadDeveloperRT));
      assertTrue(roleTypes.contains(managerRT));
      assertFalse(roleTypes.contains(productManagerRT));

      assertTrue(roleManager.hasRole(theuteUser, portalProject, developerRT));
      assertTrue(roleManager.hasRole(theuteUser, portalProject, leadDeveloperRT));
      assertTrue(roleManager.hasRole(theuteUser, portalTeamGroup, managerRT));

      // Check where anil is Lead Developer and where Developer

      roleTypes = roleManager.findUserRoleTypes(asaldhanaUser);
      assertEquals(2, roleTypes.size());
      assertTrue(roleTypes.contains(developerRT));
      assertTrue(roleTypes.contains(leadDeveloperRT));

      roleTypes = roleManager.findRoleTypes(asaldhanaUser, securityProject);
      assertEquals(2, roleTypes.size());
      assertTrue(roleTypes.contains(leadDeveloperRT));

      roleTypes = roleManager.findRoleTypes(asaldhanaUser, asProject);
      assertEquals(1, roleTypes.size());
      assertTrue(roleTypes.contains(developerRT));

      // and simpler...
      assertTrue(roleManager.hasRole(asaldhanaUser, asProject, developerRT));

      // Assert relationships

      Collection<User> identities = relationshipManager.findAssociatedUsers(portalTeamGroup, false);
      assertEquals(5, identities.size());
      assertTrue(identities.contains(claprunUser));
      assertTrue(identities.contains(mwringeUser));
      assertTrue(identities.contains(sshahUser));
      assertTrue(identities.contains(whalesUser));
      assertTrue(identities.contains(bdawidowUser));

      Collection<Group> groups = relationshipManager.findAssociatedGroups(rndDepartment, PROJECT, true, false);
      assertEquals(0, groups.size());

      // Check to which group Anil belongs
      groups = relationshipManager.findAssociatedGroups(asaldhanaUser, PEOPLE);
      assertEquals(1, groups.size());
      assertTrue(groups.contains(employeesGroup));

      // Now check sshah
      groups = relationshipManager.findAssociatedGroups(sshahUser, PEOPLE);
      assertEquals(2, groups.size());
      assertTrue(groups.contains(employeesGroup));
      assertTrue(groups.contains(portalTeamGroup));



      
      // User attributes
      Attribute[] userInfo = new Attribute[]
         {
            new SimpleAttribute(P3PConstants.INFO_USER_NAME_GIVEN, new String[]{"Boleslaw"}),
            new SimpleAttribute(P3PConstants.INFO_USER_NAME_FAMILY, new String[]{"Dawidowicz"}),
            //new SimpleAttribute("picture", new byte[][]{picture}),
            new SimpleAttribute("email", new String[]{"bd@example.com"})
         };

      AttributesManager attributesManager = session.getAttributesManager();
      attributesManager.addAttributes(bdawidowUser, userInfo);

      Map<String, Attribute> attributes = attributesManager.getAttributes(bdawidowUser);
      assertEquals(3, attributes.keySet().size());
      assertEquals("Dawidowicz", (attributes.get(P3PConstants.INFO_USER_NAME_FAMILY)).getValue());
      
      /* // Check readOnly attribute change
      userInfo = new Attribute[]
         {
            new SimpleAttribute("description", new String[]{"some description"})
         };

      attributesManager.addAttributes(bdawidowUser, userInfo);
      attributesManager.updateAttributes(bdawidowUser, userInfo);

      attributes = attributesManager.getAttributes(bdawidowUser);
      assertEquals(3, attributes.keySet().size());
      assertEquals(null, (attributes.get("description")));

      // Generate random binary data for binary attribute
      Random random = new Random();

      // Check that binary attribute picture is mapped
      AttributeDescription attributeDescription = attributesManager.getAttributeDescription(bdawidowUser, "picture");

      if (attributeDescription != null && attributeDescription.getType().equals("binary"))
      {

         // 900 kilobytes
         byte[] picture = new byte[921600];
         random.nextBytes(picture);

         userInfo = new Attribute[]
         {
            new SimpleAttribute("picture", new byte[][]{picture}),
         };


         attributesManager.addAttributes(bdawidowUser, userInfo);

         attributes = attributesManager.getAttributes(bdawidowUser);
         assertEquals(4, attributes.keySet().size());
         assertEquals("Dawidowicz", (attributes.get(P3PConstants.INFO_USER_NAME_FAMILY)).getValue());
         assertTrue(Arrays.equals((byte[])attributes.get("picture").getValue(), picture));

         // Update

         // 500 kilobytes
         picture = new byte[50600];
         random.nextBytes(picture);

         userInfo = new Attribute[]
         {
            new SimpleAttribute("picture", new byte[][]{picture}),
         };


         attributesManager.updateAttributes(bdawidowUser, userInfo);

         attributes = attributesManager.getAttributes(bdawidowUser);
         assertEquals(4, attributes.keySet().size());
         assertTrue(Arrays.equals((byte[])attributes.get("picture").getValue(), picture));
      }


      // Find user by email
      assertNull(attributesManager.findUserByUniqueAttribute("email", "toto"));
      User user = attributesManager.findUserByUniqueAttribute("email", "bd@example.com");
      assertEquals(bdawidowUser, user);


      // If email is configured as unique it should not be possible to set same value for different user
      
      attributeDescription = attributesManager.getAttributeDescription(bdawidowUser, "email");

      if (attributeDescription != null && attributeDescription.isUnique())
      {


         // check if same unique email can be used for other user
         try
         {
            userInfo = new Attribute[]
               {
                  new SimpleAttribute("email", new String[]{"bd@example.com"})
               };

            attributesManager.addAttributes(theuteUser, userInfo);
            fail();
         }
         catch (IdentityException e)
         {
            // expected
         }
      }



      // Credential
      User anotherOne = bdawidowUser; //session.getPersistenceManager().createUser("blah1");

      if (attributesManager.isCredentialTypeSupported(PasswordCredential.TYPE))
      {

         // There is a known issue that on some LDAP servers (MSAD at least) old password can
         // still be used for some time together with the new one. Because of this testsuite cannot
         // assert previously set password values

         // #1
         attributesManager.updatePassword(anotherOne, "Password2000");
         assertTrue(attributesManager.validatePassword(anotherOne, "Password2000"));
         assertFalse(attributesManager.validatePassword(anotherOne, "Password2001"));
         assertFalse(attributesManager.validatePassword(anotherOne, "Password2002"));

         // #1
         attributesManager.updatePassword(anotherOne, "Password2002");
         assertTrue(attributesManager.validatePassword(anotherOne, "Password2002"));
         assertFalse(attributesManager.validatePassword(anotherOne, "Password2001"));
         assertFalse(attributesManager.validatePassword(anotherOne, "wirdPasswordValue"));
//         assertFalse(session.getAttributesManager().validatePassword(anotherOne, "Password2000"));
         assertFalse(attributesManager.validatePassword(anotherOne, "Password2003"));


         // #1
         attributesManager.updatePassword(anotherOne, "Password2003");
         assertTrue(attributesManager.validatePassword(anotherOne, "Password2003"));
//         assertFalse(session.getAttributesManager().validatePassword(anotherOne, "Password2000"));
//         assertFalse(session.getAttributesManager().validatePassword(anotherOne, "Password2002"));
         assertFalse(attributesManager.validatePassword(anotherOne, "Password2005"));
         assertFalse(attributesManager.validatePassword(anotherOne, "Password2006"));
         assertFalse(attributesManager.validatePassword(anotherOne, "Password2007"));


         // #2
         Credential password = new PasswordCredential("SuperPassword2345");
         attributesManager.updateCredential(anotherOne, password);
         assertTrue(attributesManager.validateCredentials(anotherOne, new Credential[]{password}));

         // #3
      }

      if (attributesManager.isCredentialTypeSupported(BinaryCredential.TYPE))
      {
         // 500 kilobytes
         byte[] cert = new byte[512000];
         random.nextBytes(cert);
         Credential binaryCredential = new BinaryCredential(cert);
         attributesManager.updateCredential(anotherOne, binaryCredential);
         assertTrue(attributesManager.validateCredentials(anotherOne, new Credential[]{binaryCredential}));
      }

      persistenceManager.createUser("!(06_13_07 Sche) !(0");

      User u1 = persistenceManager.findUser("!(06_13_07 Sche) !(0");

      assertNotNull(u1);*/


      commit();
   }
   
   
   public void begin()
   {}
   
   public void commit()
   {}
}