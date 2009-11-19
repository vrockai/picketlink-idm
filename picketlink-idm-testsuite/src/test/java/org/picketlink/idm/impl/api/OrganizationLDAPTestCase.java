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

import org.picketlink.idm.api.IdentitySessionFactory;
import org.picketlink.idm.impl.LDAPTestPOJO;
import org.picketlink.idm.impl.configuration.IdentityConfigurationImpl;
import org.jboss.unit.api.pojo.annotations.Create;
import org.jboss.unit.api.pojo.annotations.Destroy;
import org.jboss.unit.api.pojo.annotations.Parameter;
import org.jboss.unit.api.pojo.annotations.Test;

/**
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class OrganizationLDAPTestCase extends LDAPTestPOJO implements APITestContext
{
   private OrganizationTest orgTest;

   private IdentitySessionFactory identitySessionFactory;

   private String samplePortalRealmName = "realm://portal/SamplePortal/DB_LDAP";

   private String sampleOrganizationRealmName = "realm://RedHat/DB_LDAP";


   @Create
   public void setUp() throws Exception
   {
      super.start();

      orgTest = new OrganizationTest(this);

      populateClean();

      identitySessionFactory = new IdentityConfigurationImpl().
         configure(getIdentityConfig()).buildIdentitySessionFactory();
   }

   @Destroy
   public void tearDown() throws Exception
   {
      super.stop();
   }

   public IdentitySessionFactory getIdentitySessionFactory()
   {
      return identitySessionFactory;
   }

   @Test
   public void testOrganization() throws Exception
   {

      orgTest.testRedHatOrganization(getSampleOrganizationRealmName());
   }

   @Test
   public void testSamplePortal() throws Exception
   {

      orgTest.testSamplePortal(getSamplePortalRealmName());

   }

   public String getSamplePortalRealmName()
   {
      return samplePortalRealmName;
   }

   @Parameter
   public void setSamplePortalRealmName(String samplePortalRealmName)
   {
      this.samplePortalRealmName = samplePortalRealmName;
   }

   public String getSampleOrganizationRealmName()
   {
      return sampleOrganizationRealmName;
   }

   @Parameter
   public void setSampleOrganizationRealmName(String sampleOrganizationRealmName)
   {
      this.sampleOrganizationRealmName = sampleOrganizationRealmName;
   }
}